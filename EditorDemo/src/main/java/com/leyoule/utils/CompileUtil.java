package com.leyoule.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * @author 45000
 */
public class CompileUtil {
    /**
     * @param name    类的绝对名称: 包名+类名
     * @param content 字符串形式的Java程序源代码
     *                sb.append("package junit.test;");
     *                sb.append("public class Test1{");
     *                sb.append("  public static void main(String[] args){");
     *                sb.append("    System.out.println(\"ok.\");");
     *                sb.append("  }");
     *                sb.append("}");
     * @return
     */
    public static Object executeMain(String name, String content) throws ClassNotFoundException {
        try {
            File file = new File("file.txt");
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //调用compile函数,对传入的源代码进行编译
        compile(name, content);
        //利用反射加载类文件
        ClassLoader classLoader = CompileUtil.class.getClassLoader();
        Class<?> claszz = classLoader.loadClass(name);
        try {
            //利用反射,执行创建出来的对象的main函数
            Method method = claszz.getMethod("main", String[].class);
            System.out.println("执行方法: "+method.getName());
            //执行方法main方法
            Object result = method.invoke(null, new Object[]{new String[]{}});
            System.out.println("返回结果 = " + result);

            //清除已经加载到内存的class文件
            method = null;
            claszz = null;
            classLoader = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param name    类的绝对名称: 包名+类名
     * @param content 字符串形式的Java程序源代码
     *                sb.append("package junit.test;");
     *                sb.append("public class Test1{");
     *                sb.append("  public static void main(String[] args){");
     *                sb.append("    System.out.println(\"ok.\");");
     *                sb.append("  }");
     *                sb.append("}");
     * @return
     */
    private final static Class<?> compile(String name, String content) {
        //Java编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //标准Java文件管理器
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        //创建自定义类对象
        StrSrcJavaObject srcObject = new StrSrcJavaObject(name, content);

        //将自定义对象转换为可遍历list集合
        Iterable<? extends JavaFileObject> fileObjects = Arrays.asList(srcObject);
        String flag = "-d";
        String outDir = "";
        try {
            //获取当前类所在的目录
            File classPath = new File(Thread.currentThread().getContextClassLoader().getResource("").toURI());
            //System.out.println("classPath = " + classPath);

            //添加一个文件分隔符
            outDir = classPath.getAbsolutePath() + File.separator;
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        Iterable<String> options = Arrays.asList(flag, outDir);
        //传入编译参数,生成真正的编译器对象
        CompilationTask task = compiler.getTask(null, fileManager, null, options, null, fileObjects);
        //调用call方法对源文件进行编译
        boolean result = task.call();
        if (result == true) {
            System.out.println("Compile it successfully.");
//            try {
//
//
//                //将编译的类文件加载到内存
//                return Class.forName(name);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
        }
        return null;
    }

    /**
     * Java动态编译辅助类
     */
    private static class StrSrcJavaObject extends SimpleJavaFileObject {
        private String content;

        public StrSrcJavaObject(String name, String content) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    public static void main(String[] args) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("package online.java;");
        sb.append("public class HelloWorld{");
        sb.append("  public static void main(String[] args){");
        sb.append("    System.out.println(\"ok.\");");
        sb.append("  }");
        sb.append("}");

        executeMain("online.java.HelloWorld", sb.toString());
    }
}
