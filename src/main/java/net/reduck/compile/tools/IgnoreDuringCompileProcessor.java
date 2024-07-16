//package net.reduck.compile.tools;
//
//import javax.annotation.processing.*;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.Element;
//import javax.lang.model.element.ExecutableElement;
//import javax.lang.model.element.TypeElement;
//import javax.tools.Diagnostic;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import javassist.*;
//
///**
// * @author Reduck
// * @since 2024/7/16 10:43
// */
//@SupportedAnnotationTypes("net.reduck.compile.tools.IgnoreDuringCompile")
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//public class IgnoreDuringCompileProcessor extends AbstractProcessor {
//
//    private final List<String> methodsToRemove = new ArrayList<>();
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        for (Element element : roundEnv.getElementsAnnotatedWith(IgnoreDuringCompile.class)) {
//            if (element instanceof ExecutableElement) {
//                TypeElement classElement = (TypeElement) element.getEnclosingElement();
//                String className = classElement.getQualifiedName().toString();
//                String methodName = element.getSimpleName().toString();
//                methodsToRemove.add(className + "#" + methodName);
//
//                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Method " + element + " will be removed.", element);
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            try {
//                processClasses();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }));
//    }
//
//    private void processClasses() throws NotFoundException, CannotCompileException, IOException {
//        ClassPool pool = ClassPool.getDefault();
//
//        for (String methodInfo : methodsToRemove) {
//            String[] parts = methodInfo.split("#");
//            String className = parts[0];
//            String methodName = parts[1];
//
//            CtClass ctClass = pool.get(className);
//            CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
//            ctClass.removeMethod(ctMethod);
//            ctClass.writeFile();
//        }
//    }
//}

package net.reduck.compile.tools;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

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
