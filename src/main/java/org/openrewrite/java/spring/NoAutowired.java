/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.java.spring;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.AnnotationMatcher;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.format.AutoFormatVisitor;
import org.openrewrite.java.format.NormalizeFormatVisitor;
import org.openrewrite.java.format.RemoveTrailingWhitespace;
import org.openrewrite.java.format.RemoveTrailingWhitespaceVisitor;
import org.openrewrite.java.tree.J;

public class NoAutowired extends Recipe {

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new NoAutowiredAnnotationsVisitor();
    }

    private class NoAutowiredAnnotationsVisitor extends JavaIsoVisitor<ExecutionContext> {
        private final AnnotationMatcher annotationMatcher = new AnnotationMatcher("@org.springframework.beans.factory.annotation.Autowired");

        public NoAutowiredAnnotationsVisitor() {
            setCursoringOn();
        }

        @Override
        public J.MethodDecl visitMethod(J.MethodDecl method, ExecutionContext executionContext) {
            J.MethodDecl m = super.visitMethod(method, executionContext);
            m = m.withAnnotations(ListUtils.map(m.getAnnotations(), a -> annotationMatcher.matches(a) ? null : a));
            if (m.getAnnotations() != method.getAnnotations()) {
                m = (J.MethodDecl) new AutoFormatVisitor<>().visit(m, executionContext, getCursor().getParentOrThrow());
            }
            return m;
        }
    }
}

