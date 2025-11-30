/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.internal.service;

import org.gradle.internal.concurrent.CompositeStoppable;
import org.gradle.internal.concurrent.Stoppable;
import org.jspecify.annotations.Nullable;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.gradle.util.internal.CollectionUtils.collect;
import static org.gradle.util.internal.CollectionUtils.join;

/**
 * A hierarchical {@link ServiceRegistry} implementation.
 *
 * <p>Service instances are closed when the registry that created them is closed using {@link #close()}.
 * If a service instance implements {@link Closeable} or {@link Stoppable}
 * then the appropriate {@link Closeable#close()} or {@link Stoppable#stop()} method is called.
 * Instances are closed in reverse dependency order.
 *
 * <p>Service registries are arranged in a hierarchy. If a service of a given type cannot be located, the registry uses its parent registry, if any, to locate the service.</p>
 *
 * <p>Service interfaces should be annotated with {@link org.gradle.internal.service.scopes.ServiceScope} to indicate their intended usage.</p>
 *
 * <p>Service interfaces can be annotated with {@link org.gradle.internal.service.scopes.StatefulListener} to indicate that services instances that implement the interface should
 * be registered as a listener of that type. Alternatively, service implementations can be annotated with {@link org.gradle.internal.service.scopes.ListenerService} to indicate that the should be
 * registered as a listener.</p>
 */
class OwnServices implements ServiceProvider {
    private static final ServicesSnapshot EMPTY = new ServicesSnapshot(null, new AnnotatedServiceLifecycleHandler[0]);
    private final Map<Class<?>, List<ServiceProvider>> providersByType = new HashMap<Class<?>, List<ServiceProvider>>(16, 0.5f);
    private final CompositeStoppable stoppable = CompositeStoppable.stoppable();
    private final AtomicReference<ServicesSnapshot> services = new AtomicReference<>(EMPTY);

    public OwnServices() {
        providersByType.put(ServiceRegistry.class, Collections.<ServiceProvider>singletonList(new ThisAsService(ServiceAccess.getPublicScope())));
    }

    @Override
    public Service getService(Type type, @Nullable ServiceAccessToken token) {
        List<ServiceProvider> serviceProviders = getProviders(unwrap(type));
        if (serviceProviders.isEmpty()) {
            return null;
        }
        if (serviceProviders.size() == 1) {
            return serviceProviders.get(0).getService(type, token);
        }

        List<Service> services = new ArrayList<Service>(serviceProviders.size());
        for (ServiceProvider serviceProvider : serviceProviders) {
            Service service = serviceProvider.getService(type, token);
            if (service != null) {
                services.add(service);
            }
        }

        if (services.isEmpty()) {
            return null;
        }

        if (services.size() == 1) {
            return services.get(0);
        }

        Set<String> descriptions = new TreeSet<String>();
        for (Service candidate : services) {
            descriptions.add(candidate.getDisplayName());
        }

        Formatter formatter = new Formatter();
        formatter.format("Multiple services of type %s available in %s:", format(type), getDisplayName());
        for (String description : descriptions) {
            formatter.format("%n   - %s", description);
        }
        throw new ServiceLookupException(formatter.toString());
    }

    private List<ServiceProvider> getProviders(Class<?> type) {
        List<ServiceProvider> providers = providersByType.get(type);
        return providers == null ? Collections.<ServiceProvider>emptyList() : providers;
    }

    @Override
    public Visitor getAll(Class<?> serviceType, @Nullable ServiceAccessToken token, Visitor visitor) {
        for (ServiceProvider serviceProvider : getProviders(serviceType)) {
            visitor = serviceProvider.getAll(serviceType, token, visitor);
        }
        return visitor;
    }

    @Override
    public void stop() {
        stoppable.stop();
    }

    public void add(SingletonService serviceProvider) {
        assertMutable();
        stoppable.add(serviceProvider);
        collectProvidersForClassHierarchy(inspector, serviceProvider.getDeclaredServiceTypes(), serviceProvider);
        notifyAnnotationHandler(serviceProvider);
    }

    private void notifyAnnotationHandler(SingletonService serviceProvider) {
        for (AnnotatedServiceLifecycleHandler annotationHandler : services.updateAndGet(it -> it.addService(serviceProvider)).lifecycleHandlers) {
            notifyAnnotationHandler(annotationHandler, serviceProvider);
        }
    }

    public void collectProvidersForClassHierarchy(ClassInspector inspector, List<Class<?>> declaredServiceTypes, ServiceProvider serviceProvider) {
        for (Class<?> serviceType : declaredServiceTypes) {
            for (Class<?> type : inspector.getHierarchy(serviceType)) {
                if (type.equals(Object.class)) {
                    continue;
                }
                if (type.equals(ServiceRegistry.class)) {
                    // Disallow custom services of type ServiceRegistry, as these are automatically provided
                    throw new IllegalArgumentException("Cannot define a service of type ServiceRegistry: " + serviceProvider);
                }
                putServiceType(type, serviceProvider);
            }
        }
    }

    private void putServiceType(Class<?> type, ServiceProvider serviceProvider) {
        List<ServiceProvider> serviceProviders = providersByType.get(type);
        if (serviceProviders == null) {
            serviceProviders = new ArrayList<ServiceProvider>(2);
            serviceProviders.add(serviceProvider);
            providersByType.put(type, serviceProviders);
            return;
        }

        // Adding of the service provider for the same type may happen when it has multiple declared service types
        if (!serviceProviders.contains(serviceProvider)) {
            serviceProviders.add(serviceProvider);
        }
    }

    public void instanceRealized(List<Class<?>> declaredServiceTypes, Supplier<String> displayName, Object instance) {
        if (instance instanceof AnnotatedServiceLifecycleHandler && !isAssignableFromAnyType(AnnotatedServiceLifecycleHandler.class, declaredServiceTypes)) {
            throw new IllegalStateException(String.format("%s implements %s but is not declared as a service of this type. This service is declared as having %s.",
                displayName.get(), AnnotatedServiceLifecycleHandler.class.getSimpleName(), format("type", declaredServiceTypes)));
        }
        if (instance instanceof AnnotatedServiceLifecycleHandler) {
            annotationHandlerCreated((AnnotatedServiceLifecycleHandler) instance);
        }
        for (AnnotatedServiceLifecycleHandler lifecycleHandler : services.get().lifecycleHandlers) {
            for (Class<? extends Annotation> annotation : lifecycleHandler.getAnnotations()) {
                boolean implementationHasAnnotation = inspector.hasAnnotation(instance.getClass(), annotation);
                boolean declaredWithAnnotation = anyTypeHasAnnotation(annotation, declaredServiceTypes);
                if (implementationHasAnnotation && !declaredWithAnnotation) {
                    throw new IllegalStateException(String.format("%s is annotated with @%s but is not declared as a service with this annotation. This service is declared as having %s.",
                        displayName.get(), format(annotation), format("type", declaredServiceTypes)));
                }
            }
        }
    }

    void annotationHandlerCreated(AnnotatedServiceLifecycleHandler annotationHandler) {
        ServicesSnapshot snapshot = services.updateAndGet(it -> it.addLifecycleHandler(annotationHandler));
        ServiceList list = snapshot.services;
        while (list != null) {
            notifyAnnotationHandler(annotationHandler, list.service);
            list = list.next;
        }
    }

    private void notifyAnnotationHandler(AnnotatedServiceLifecycleHandler annotationHandler, SingletonService candidate) {
        if (annotationHandler.getImplicitAnnotation() != null) {
            annotationHandler.whenRegistered(annotationHandler.getImplicitAnnotation(), new RegistrationWrapper(candidate));
        } else {
            List<Class<?>> declaredServiceTypes = candidate.getDeclaredServiceTypes();
            for (Class<? extends Annotation> annotation : annotationHandler.getAnnotations()) {
                if (anyTypeHasAnnotation(annotation, declaredServiceTypes)) {
                    annotationHandler.whenRegistered(annotation, new RegistrationWrapper(candidate));
                }
            }
        }
    }

    private boolean anyTypeHasAnnotation(Class<? extends Annotation> annotation, List<Class<?>> types) {
        for (Class<?> type : types) {
            if (inspector.hasAnnotation(type, annotation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    /**
     * Carries a snapshot of the current set of services and lifecycle handlers so they can change together.
     *
     * Lifecycle handlers are maintained in a copy-on-write array since there are at most 3 lifecycle handler instances
     * per registry, and they are iterated frequently (for every service registration).
     *
     * Services are maintained in a linked list since there are many, they are frequently written and iterated very
     * rarely (once per lifecycle handler).
     */
    private static class ServicesSnapshot {
        final @Nullable ServiceList services;
        final AnnotatedServiceLifecycleHandler[] lifecycleHandlers;

        ServicesSnapshot(@Nullable ServiceList services, AnnotatedServiceLifecycleHandler[] lifecycleHandlers) {
            this.services = services;
            this.lifecycleHandlers = lifecycleHandlers;
        }

        ServicesSnapshot addService(SingletonService service) {
            return new ServicesSnapshot(
                new ServiceList(service, services),
                lifecycleHandlers
            );
        }

        ServicesSnapshot addLifecycleHandler(AnnotatedServiceLifecycleHandler lifecycleHandler) {
            return new ServicesSnapshot(
                services,
                append(lifecycleHandlers, lifecycleHandler)
            );
        }

        private static AnnotatedServiceLifecycleHandler[] append(AnnotatedServiceLifecycleHandler[] array, AnnotatedServiceLifecycleHandler annotationHandler) {
            AnnotatedServiceLifecycleHandler[] newArray = new AnnotatedServiceLifecycleHandler[array.length + 1];
            System.arraycopy(array, 0, newArray, 0, array.length);
            newArray[array.length] = annotationHandler;
            return newArray;
        }
    }

    private static abstract class ManagedObjectServiceProvider implements ServiceProvider, Service {
        protected final DefaultServiceRegistry owner;
        private final Queue<ServiceProvider> dependents = new ConcurrentLinkedQueue<ServiceProvider>();
        private volatile Object instance;

        protected ManagedObjectServiceProvider(DefaultServiceRegistry owner) {
            this.owner = owner;
        }

        abstract List<Class<?>> getDeclaredServiceTypes();

        protected void instanceRealized(Object instance) {
            owner.ownServices.instanceRealized(getDeclaredServiceTypes(), this::getDisplayName, instance);
        }

        protected void setInstance(Object instance) {
            instanceRealized(instance);
            // Only expose the instance after we're done with initialization.
            this.instance = instance;
        }

        public final Object getInstance() {
            Object result = instance;
            if (result == null) {
                synchronized (this) {
                    result = instance;
                    if (result == null) {
                        setInstance(createServiceInstance());
                        result = instance;
                    }
                }
            }
            return result;
        }

        /**
         * Subclasses implement this method to create the service instance. It is never called concurrently and may not return null.
         */
        protected abstract Object createServiceInstance();

        @Override
        public final void requiredBy(ServiceProvider serviceProvider) {
            if (fromSameRegistry(serviceProvider)) {
                dependents.add(serviceProvider);
            }
        }

        private boolean fromSameRegistry(ServiceProvider serviceProvider) {
            return serviceProvider instanceof ManagedObjectServiceProvider && ((ManagedObjectServiceProvider) serviceProvider).owner == owner;
        }

        @Override
        public final synchronized void stop() {
            try {
                if (instance != null) {
                    CompositeStoppable.stoppable(dependents).add(instance).stop();
                }
            } finally {
                dependents.clear();
                instance = null;
            }
        }
    }


    static abstract class SingletonService extends ManagedObjectServiceProvider {
        private enum BindState {UNBOUND, BINDING, BOUND}

        protected final ServiceAccessScope accessScope;
        protected final List<? extends Type> serviceTypes;
        private final List<Class<?>> serviceTypesAsClasses;

        BindState state = BindState.UNBOUND;

        SingletonService(DefaultServiceRegistry owner, ServiceAccessScope accessScope, List<? extends Type> serviceTypes) {
            super(owner);

            if (serviceTypes.isEmpty()) {
                throw new IllegalArgumentException("Expected at least one declared service type");
            }

            this.accessScope = accessScope;
            this.serviceTypes = serviceTypes;
            serviceTypesAsClasses = collect(serviceTypes, DefaultServiceRegistry::unwrap);
        }

        @Override
        List<Class<?>> getDeclaredServiceTypes() {
            return serviceTypesAsClasses;
        }

        @Override
        public String getDisplayName() {
            return format("Service", serviceTypes);
        }

        @Override
        public String toString() {
            return getDisplayName();
        }

        @Override
        public Object get() {
            return getInstance();
        }

        private Object getPreparedInstance() {
            return prepare().get();
        }

        private Service prepare() {
            if (state == BindState.BOUND) {
                return this;
            }
            synchronized (this) {
                if (state == BindState.BINDING) {
                    throw new ServiceValidationException("Cycle in dependencies of " + getDisplayName() + " detected");
                }
                if (state == BindState.UNBOUND) {
                    state = BindState.BINDING;
                    try {
                        bind();
                        state = BindState.BOUND;
                    } catch (RuntimeException e) {
                        state = BindState.UNBOUND;
                        throw e;
                    }
                }
                return this;
            }
        }

        /**
         * Do any preparation work and validation to ensure that {@link #createServiceInstance()} can be called later.
         * This method is never called concurrently.
         */
        protected void bind() {
        }

        @Override
        public Service getService(Type serviceType, @Nullable ServiceAccessToken token) {
            if (!accessScope.contains(token)) {
                return null;
            }
            if (!isSatisfiedByAny(serviceType, serviceTypes)) {
                return null;
            }
            return prepare();
        }

        @Override
        public Visitor getAll(Class<?> serviceType, @Nullable ServiceAccessToken token, Visitor visitor) {
            if (!accessScope.contains(token)) {
                return visitor;
            }
            if (isAssignableFromAnyType(serviceType, serviceTypesAsClasses)) {
                visitor.visit(prepare());
            }
            return visitor;
        }
    }

    private static class ServiceList {
        final SingletonService service;
        final @Nullable ServiceList next;

        ServiceList(SingletonService head, @Nullable ServiceList next) {
            this.service = head;
            this.next = next;
        }
    }
}
