package net.reduck.compile.tools;

import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author Reduck
 * @since 2024/7/16 10:42
 */
@SupportedAnnotationTypes("net.reduck.compile.tools.IgnoreDuringCompile")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class IgnoreDuringCompileProcessor extends AbstractProcessor {

    private Trees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(IgnoreDuringCompile.class)) {
            if (element instanceof ExecutableElement) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Method " + element + " will be removed.", element);
                removeMethod(element);
            }
        }
        return true;
    }

    private void removeMethod(Element element) {
        JCTree tree = (JCTree) trees.getTree(element);
        if (tree == null) {
            return;
        }

        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) trees.getTree(element.getEnclosingElement());
        if (classDecl == null) {
            return;
        }

        List<JCTree> members = classDecl.defs;
        List<JCTree> newMembers = List.nil();

        for (JCTree member : members) {
            if (member != tree) {
                newMembers = newMembers.append(member);
            }
        }

        classDecl.defs = newMembers;
    }
}
