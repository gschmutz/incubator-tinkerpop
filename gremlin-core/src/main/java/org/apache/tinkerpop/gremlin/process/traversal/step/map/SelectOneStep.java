/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process.traversal.step.map;

import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.lambda.IdentityTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.PathProcessor;
import org.apache.tinkerpop.gremlin.process.traversal.step.Scoping;
import org.apache.tinkerpop.gremlin.process.traversal.step.TraversalParent;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.TraverserRequirement;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalUtil;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class SelectOneStep<S, E> extends MapStep<S, E> implements TraversalParent, Scoping, PathProcessor {

    private Scope scope;
    private final String selectLabel;
    private Traversal.Admin<Object, Object> selectTraversal = new IdentityTraversal<>();

    public SelectOneStep(final Traversal.Admin traversal, final Scope scope, final String selectLabel) {
        super(traversal);
        this.scope = scope;
        this.selectLabel = selectLabel;
    }

    @Override
    protected E map(final Traverser.Admin<S> traverser) {
        return (E) TraversalUtil.apply((Object) this.getScopeValueByKey(this.selectLabel, traverser), this.selectTraversal);
    }

    @Override
    public String toString() {
        return StringFactory.stepString(this, this.scope, this.selectLabel, this.selectTraversal);
    }

    @Override
    public SelectOneStep<S, E> clone() {
        final SelectOneStep<S, E> clone = (SelectOneStep<S, E>) super.clone();
        clone.selectTraversal = this.selectTraversal.clone();
        return clone;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.scope.hashCode() ^ this.selectLabel.hashCode() ^ this.selectTraversal.hashCode();
    }

    @Override
    public List<Traversal.Admin<Object, Object>> getLocalChildren() {
        return Collections.singletonList(this.selectTraversal);
    }

    @Override
    public void addLocalChild(final Traversal.Admin<?, ?> selectTraversal) {
        this.selectTraversal = this.integrateChild(selectTraversal);
    }

    @Override
    public Set<TraverserRequirement> getRequirements() {
        return this.getSelfAndChildRequirements(this.scope == Scope.local ? TraverserRequirement.OBJECT : TraverserRequirement.PATH);
    }

    @Override
    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope recommendNextScope() {
        return Scope.global;
    }

    @Override
    public Scope getScope() {
        return this.scope;
    }
}


