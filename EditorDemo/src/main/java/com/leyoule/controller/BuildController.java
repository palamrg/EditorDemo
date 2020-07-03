package com.leyoule.controller;

import com.leyoule.utils.CompileUtil;
import org.springframework.web.bind.annotation.*;

import java.io.*;

/**
 * @author 45000
 * @javadoc RestController 该注解相当于对全部方法添加@ResponseBody注解
 * 已成功部署访问
 * http://127.0.0.1:11000/Editor/java/compile?code=&fileExt=
 */
@RestController
@RequestMapping("/java")
public class BuildController {
    /**
     * 文件后缀
     */
    public final static String FileExt = "java";
    /**
     * 用来重命名文件的序号
     */
    public static int INDEX = 0;
    /**
     * 用来获取输出在屏幕上的结果
     */
    public static String strResult = "";
    /**
     * 类的包名,需要与前台一致
     */
    public static String classPackage = "online.java.HelloWorld";

    public BuildController() {

    }

    /**
     * 使用该接口进行Java代码测试输出
     * http://localhost:8080/java/compile?code=&fileExt=
     *
     * @param code
     * @param fileExt
     * @return
     */
    @GetMapping("/compile")
    public OutResult test(
            @RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "fileExt", required = true) String fileExt)
            throws InterruptedException, ClassNotFoundException {
        //替换系统默认的输出流System.out的方法
        replacePrintStream();


        //输出结果
        OutResult result = new OutResult();
        //判断是否是Java字符串文本
        if (FileExt.equalsIgnoreCase(fileExt)) {
            code = code.replace("\r", "").replace("\n", "").replace("\t", "");
            //处理字符串变更名字,保证类名称不重复
            String longClassName = classPackage + INDEX;
            String shortClassName = "HelloWorld" + INDEX;
            INDEX++;
            code = code.replace("HelloWorld", shortClassName);

            System.out.println("短类名 = " + shortClassName);
            System.out.println("长类名 = " + longClassName);

            CompileUtil.executeMain(longClassName, code);
            result.setOutput("以下为运行结果: \r\n" + strResult.trim());
            result.setErrors("");

            //清除垃圾
            code = null;
            System.gc();
        } else {
            result.setOutput("必须为java代码才能运行!" + code);
            result.setErrors("<b>400</b>");
        }
        PrintStream out = System.out;

        return result;
    }

    private void replacePrintStream() {
        //替换系统本身的输出六System.out.println()方法  //将原来的System.out交给printStream 对象保存
        PrintStream oldPrintStream = System.out;
        ByteArrayOutputStream bos = new MyOutStream();
        //设置新的out
        System.setOut(new PrintStream(bos));
        //System.out.println("this is the text to output");
    }

    /**
     * @author 45000
     * 用来替换打印流,将输出捕获后输出到前台
     */
    class MyOutStream extends ByteArrayOutputStream {
        @Override
        public void write(byte[] b) throws IOException {
            super.write(b);
            BuildController.strResult = new String(this.buf);
        }

        @Override
        public synchronized void write(int b) {
            super.write(b);
            BuildController.strResult = new String(this.buf);
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) {
            super.write(b, off, len);
            BuildController.strResult = new String(this.buf);
        }
    }

    /**
     * 返回的结果类
     */
    private class OutResult {
        private Object output;
        private Object errors;

        public Object getOutput() {
            return output;
        }

        public void setOutput(Object output) {
            this.output = output;
        }

        public Object getErrors() {
            return errors;
        }

        public void setErrors(Object errors) {
            this.errors = errors;
        }
    }
}
