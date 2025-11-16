/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.integtests.fixtures.executor

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import spock.lang.Issue

// This is a workaround on our side
@Issue('https://youtrack.jetbrains.com/issue/KT-29546')
class IgnoreKotlinCompilerWarningIntegrationTest extends AbstractIntegrationSpec {
    void dontWarnOnKotlinCompilerWarning() {
        buildFile << '''
System.err.println(\'''
Compilation with Kotlin compile daemon was not successful
java.rmi.UnmarshalException: Error unmarshaling return header; nested exception is:
\tjava.io.EOFException
\tat java.rmi/sun.rmi.transport.StreamRemoteCall.executeCall(StreamRemoteCall.java:254)
\tat java.rmi/sun.rmi.server.UnicastRef.invoke(UnicastRef.java:164)
\tat java.rmi/java.rmi.server.RemoteObjectInvocationHandler.invokeRemoteMethod(RemoteObjectInvocationHandler.java:217)
\tat java.rmi/java.rmi.server.RemoteObjectInvocationHandler.invoke(RemoteObjectInvocationHandler.java:162)
\tat com.sun.proxy.$Proxy82.compile(Unknown Source)
\tat org.jetbrains.kotlin.compilerRunner.GradleKotlinCompilerWork.incrementalCompilationWithDaemon(GradleKotlinCompilerWork.kt:282)
\tat org.jetbrains.kotlin.compilerRunner.GradleKotlinCompilerWork.compileWithDaemon(GradleKotlinCompilerWork.kt:195)
\tat org.jetbrains.kotlin.compilerRunner.GradleKotlinCompilerWork.compileWithDaemonOrFallbackImpl(GradleKotlinCompilerWork.kt:134)
\tat org.jetbrains.kotlin.compilerRunner.GradleKotlinCompilerWork.run(GradleKotlinCompilerWork.kt:117)
\tat org.jetbrains.kotlin.compilerRunner.GradleCompilerRunner.runCompilerAsync(GradleKotlinCompilerRunner.kt:148)
\tat org.jetbrains.kotlin.compilerRunner.GradleCompilerRunner.runCompilerAsync(GradleKotlinCompilerRunner.kt:143)
\tat org.jetbrains.kotlin.compilerRunner.GradleCompilerRunner.runJvmCompilerAsync(GradleKotlinCompilerRunner.kt:83)
\tat org.jetbrains.kotlin.gradle.tasks.KotlinCompile.callCompilerAsync$kotlin_gradle_plugin(Tasks.kt:422)
\tat org.jetbrains.kotlin.gradle.tasks.KotlinCompile.callCompilerAsync$kotlin_gradle_plugin(Tasks.kt:345)
\tat org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile.executeImpl(Tasks.kt:306)
\tat org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile.execute(Tasks.kt:277)
\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
\tat java.base/java.lang.reflect.Method.invoke(Method.java:564)
\tat org.gradle.internal.reflect.JavaMethod.invoke(JavaMethod.java:104)
\tat org.gradle.api.internal.project.taskfactory.IncrementalTaskInputsTaskAction.doExecute(IncrementalTaskInputsTaskAction.java:47)
\tat org.gradle.api.internal.project.taskfactory.StandardTaskAction.execute(StandardTaskAction.java:42)
\tat org.gradle.api.internal.project.taskfactory.AbstractIncrementalTaskAction.execute(AbstractIncrementalTaskAction.java:25)
\tat org.gradle.api.internal.project.taskfactory.StandardTaskAction.execute(StandardTaskAction.java:28)
\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecutor$3.run(ExecuteActionsTaskExecutor.java:569)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$RunnableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:395)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$RunnableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:387)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$1.execute(DefaultBuildOperationExecutor.java:157)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:242)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:150)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:84)
\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecutor.executeAction(ExecuteActionsTaskExecutor.java:554)
\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecutor.executeActions(ExecuteActionsTaskExecutor.java:537)
\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecutor.access$300(ExecuteActionsTaskExecutor.java:108)
\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecutor$TaskExecution.executeWithPreviousOutputFiles(ExecuteActionsTaskExecutor.java:278)
\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecutor$TaskExecution.execute(ExecuteActionsTaskExecutor.java:267)
\tat org.gradle.internal.execution.steps.ExecuteStep.lambda$execute$0(ExecuteStep.java:32)
\tat java.base/java.util.Optional.map(Optional.java:258)
\tat org.gradle.internal.execution.steps.ExecuteStep.execute(ExecuteStep.java:32)
\tat org.gradle.internal.execution.steps.ExecuteStep.execute(ExecuteStep.java:26)
\tat org.gradle.internal.execution.steps.CleanupOutputsStep.execute(CleanupOutputsStep.java:67)
\tat org.gradle.internal.execution.steps.CleanupOutputsStep.execute(CleanupOutputsStep.java:36)
\tat org.gradle.internal.execution.steps.ResolveInputChangesStep.execute(ResolveInputChangesStep.java:49)
\tat org.gradle.internal.execution.steps.ResolveInputChangesStep.execute(ResolveInputChangesStep.java:34)
\tat org.gradle.internal.execution.steps.CancelExecutionStep.execute(CancelExecutionStep.java:43)
\tat org.gradle.internal.execution.steps.TimeoutStep.executeWithoutTimeout(TimeoutStep.java:73)
\tat org.gradle.internal.execution.steps.TimeoutStep.execute(TimeoutStep.java:54)
\tat org.gradle.internal.execution.steps.CreateOutputsStep.execute(CreateOutputsStep.java:44)
\tat org.gradle.internal.execution.steps.SnapshotOutputsStep.execute(SnapshotOutputsStep.java:54)
\tat org.gradle.internal.execution.steps.SnapshotOutputsStep.execute(SnapshotOutputsStep.java:38)
\tat org.gradle.internal.execution.steps.BroadcastChangingOutputsStep.execute(BroadcastChangingOutputsStep.java:49)
\tat org.gradle.internal.execution.steps.CacheStep.executeWithoutCache(CacheStep.java:159)
\tat org.gradle.internal.execution.steps.CacheStep.execute(CacheStep.java:72)
\tat org.gradle.internal.execution.steps.CacheStep.execute(CacheStep.java:43)
\tat org.gradle.internal.execution.steps.StoreExecutionStateStep.execute(StoreExecutionStateStep.java:44)
\tat org.gradle.internal.execution.steps.StoreExecutionStateStep.execute(StoreExecutionStateStep.java:33)
\tat org.gradle.internal.execution.steps.RecordOutputsStep.execute(RecordOutputsStep.java:38)
\tat org.gradle.internal.execution.steps.RecordOutputsStep.execute(RecordOutputsStep.java:24)
\tat org.gradle.internal.execution.steps.SkipUpToDateStep.executeBecause(SkipUpToDateStep.java:92)
\tat org.gradle.internal.execution.steps.SkipUpToDateStep.lambda$execute$0(SkipUpToDateStep.java:85)
\tat java.base/java.util.Optional.map(Optional.java:258)
\tat org.gradle.internal.execution.steps.SkipUpToDateStep.execute(SkipUpToDateStep.java:55)
\tat org.gradle.internal.execution.steps.SkipUpToDateStep.execute(SkipUpToDateStep.java:39)
\tat org.gradle.internal.execution.steps.ResolveChangesStep.execute(ResolveChangesStep.java:76)
\tat org.gradle.internal.execution.steps.ResolveChangesStep.execute(ResolveChangesStep.java:37)
\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsFinishedStep.execute(MarkSnapshottingInputsFinishedStep.java:36)
\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsFinishedStep.execute(MarkSnapshottingInputsFinishedStep.java:26)
\tat org.gradle.internal.execution.steps.ResolveCachingStateStep.execute(ResolveCachingStateStep.java:94)
\tat org.gradle.internal.execution.steps.ResolveCachingStateStep.execute(ResolveCachingStateStep.java:49)
\tat org.gradle.internal.execution.steps.CaptureStateBeforeExecutionStep.execute(CaptureStateBeforeExecutionStep.java:79)
\tat org.gradle.internal.execution.steps.CaptureStateBeforeExecutionStep.execute(CaptureStateBeforeExecutionStep.java:53)
\tat org.gradle.internal.execution.steps.ValidateStep.execute(ValidateStep.java:74)
\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.lambda$execute$2(SkipEmptyWorkStep.java:78)
\tat java.base/java.util.Optional.orElseGet(Optional.java:362)
\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.execute(SkipEmptyWorkStep.java:78)
\tat org.gradle.internal.execution.steps.SkipEmptyWorkStep.execute(SkipEmptyWorkStep.java:34)
\tat org.gradle.internal.execution.steps.legacy.MarkSnapshottingInputsStartedStep.execute(MarkSnapshottingInputsStartedStep.java:39)
\tat org.gradle.internal.execution.steps.LoadExecutionStateStep.execute(LoadExecutionStateStep.java:40)
\tat org.gradle.internal.execution.steps.LoadExecutionStateStep.execute(LoadExecutionStateStep.java:28)
\tat org.gradle.internal.execution.impl.DefaultWorkExecutor.execute(DefaultWorkExecutor.java:33)
\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecutor.executeIfValid(ExecuteActionsTaskExecutor.java:194)
\tat org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecutor.execute(ExecuteActionsTaskExecutor.java:186)
\tat org.gradle.api.internal.tasks.execution.CleanupStaleOutputsExecutor.execute(CleanupStaleOutputsExecutor.java:114)
\tat org.gradle.api.internal.tasks.execution.FinalizePropertiesTaskExecutor.execute(FinalizePropertiesTaskExecutor.java:46)
\tat org.gradle.api.internal.tasks.execution.ResolveTaskExecutionModeExecutor.execute(ResolveTaskExecutionModeExecutor.java:62)
\tat org.gradle.api.internal.tasks.execution.SkipTaskWithNoActionsExecutor.execute(SkipTaskWithNoActionsExecutor.java:57)
\tat org.gradle.api.internal.tasks.execution.SkipOnlyIfTaskExecutor.execute(SkipOnlyIfTaskExecutor.java:56)
\tat org.gradle.api.internal.tasks.execution.CatchExceptionTaskExecutor.execute(CatchExceptionTaskExecutor.java:36)
\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecutor$1.executeTask(EventFiringTaskExecutor.java:77)
\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecutor$1.call(EventFiringTaskExecutor.java:55)
\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecutor$1.call(EventFiringTaskExecutor.java:52)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$CallableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:409)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$CallableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:399)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor$1.execute(DefaultBuildOperationExecutor.java:157)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:242)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:150)
\tat org.gradle.internal.operations.DefaultBuildOperationExecutor.call(DefaultBuildOperationExecutor.java:94)
\tat org.gradle.api.internal.tasks.execution.EventFiringTaskExecutor.execute(EventFiringTaskExecutor.java:52)
\tat org.gradle.execution.plan.LocalTaskNodeExecutor.execute(LocalTaskNodeExecutor.java:41)
\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:370)
\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$InvokeNodeExecutorsAction.execute(DefaultTaskExecutionGraph.java:357)
\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:350)
\tat org.gradle.execution.taskgraph.DefaultTaskExecutionGraph$BuildOperationAwareExecutionAction.execute(DefaultTaskExecutionGraph.java:336)
\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.lambda$run$0(DefaultPlanExecutor.java:127)
\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.execute(DefaultPlanExecutor.java:191)
\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.executeNextNode(DefaultPlanExecutor.java:182)
\tat org.gradle.execution.plan.DefaultPlanExecutor$ExecutorWorker.run(DefaultPlanExecutor.java:124)
\tat org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
\tat org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)
\tat java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1130)
\tat java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:630)
\tat org.gradle.internal.concurrent.ThreadFactoryImpl$ManagedThreadRunnable.run(ThreadFactoryImpl.java:56)
\tat java.base/java.lang.Thread.run(Thread.java:832)
Caused by: java.io.EOFException
\tat java.base/java.io.DataInputStream.readByte(DataInputStream.java:271)
\tat java.rmi/sun.rmi.transport.StreamRemoteCall.executeCall(StreamRemoteCall.java:240)
\t... 116 more
\''')
'''
        expect:
        succeeds("help")
    }
}
