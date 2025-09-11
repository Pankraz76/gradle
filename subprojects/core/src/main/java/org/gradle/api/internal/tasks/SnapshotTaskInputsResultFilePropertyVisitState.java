/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.api.internal.tasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import org.gradle.api.internal.tasks.properties.InputFilePropertySpec;
import org.gradle.api.internal.tasks.properties.PropertySpec;
import org.gradle.internal.fingerprint.CurrentFileCollectionFingerprint;
import org.gradle.internal.snapshot.DirectorySnapshot;
import org.gradle.operations.execution.FilePropertyVisitor;

import java.util.Map;
import java.util.Set;

public class SnapshotTaskInputsResultFilePropertyVisitState extends BaseFilePropertyVisitState implements FilePropertyVisitor.VisitState {

    private final SnapshotTaskInputsBuildOperationType.Result.InputFilePropertyVisitor visitor;

    public static void visitInputFileProperties(ImmutableSortedMap<String, CurrentFileCollectionFingerprint> inputFileProperties, SnapshotTaskInputsBuildOperationType.Result.InputFilePropertyVisitor visitor, Set<InputFilePropertySpec> inputFilePropertySpecs) {
        ImmutableMap<String, InputFilePropertySpec> propertySpecsByName = Maps.uniqueIndex(inputFilePropertySpecs, PropertySpec::getPropertyName);
        SnapshotTaskInputsResultFilePropertyVisitState state = new SnapshotTaskInputsResultFilePropertyVisitState(visitor, propertySpecsByName);
        for (Map.Entry<String, CurrentFileCollectionFingerprint> entry : inputFileProperties.entrySet()) {
            CurrentFileCollectionFingerprint fingerprint = entry.getValue();

            state.propertyName = entry.getKey();
            state.propertyHash = fingerprint.getHash();
            state.fingerprints = fingerprint.getFingerprints();

            visitor.preProperty(state);
            fingerprint.getSnapshot().accept(state);
            visitor.postProperty();
        }
    }

    private SnapshotTaskInputsResultFilePropertyVisitState(SnapshotTaskInputsBuildOperationType.Result.InputFilePropertyVisitor visitor, Map<String, InputFilePropertySpec> propertySpecsByName) {
        super(propertySpecsByName);
        this.visitor = visitor;
    }

    @Override
    protected void preRoot() {
        visitor.preRoot(this);
    }

    @Override
    protected void postRoot() {
        visitor.postRoot();
    }

    @Override
    protected void preDirectory() {
        visitor.preDirectory(this);
    }

    @Override
    protected void preUnvisitedDirectory(DirectorySnapshot unvisited) {
        visitor.preDirectory(new TaskDirectoryVisitState(unvisited, this));
    }

    @Override
    protected void postDirectory() {
        visitor.postDirectory();
    }

    @Override
    protected void file() {
        visitor.file(this);
    }

    private static class TaskDirectoryVisitState extends DirectoryVisitState<FilePropertyVisitor.VisitState> implements FilePropertyVisitor.VisitState {

        public TaskDirectoryVisitState(DirectorySnapshot unvisited, FilePropertyVisitor.VisitState delegate) {
            super(unvisited, delegate);
        }
    }
}
