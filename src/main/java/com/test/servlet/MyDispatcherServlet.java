package com.test.servlet;

import com.test.annotation.MyAutowired;
import com.test.annotation.MyController;
import com.test.annotation.MyRequestMapping;
import com.test.annotation.MyService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class MyDispatcherServlet extends HttpServlet {

    // 存在当前加载的所有的类
    List<String> classNames = new ArrayList<String>();

    // 存储IOC容器的MAP
    Map<String, Object> beans = new HashMap<>();

    Map<String, Object> instances = new HashMap<>();

    // 存储路径和方法的映射关系
    Map<String, Object> handlerMap = new HashMap<String, Object>();

    public MyDispatcherServlet() {
        System.out.println("DispatchServlet()........");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("init()............");

        // 1.扫描需要的实例化的类
        doScanPackage("com.test");
        System.out.println("当前文件下所有的class类.......");
        for (String name : classNames) {
            System.out.println(name);
        }

        // 2.实例化
        doInstance();
        System.out.println("当前实例化的对象信息.........");
        for (Map.Entry<String, Object> map : beans.entrySet()) {
            System.out.println("key:" + map.getKey() + "; value:" + map.getValue());
        }

        // 3.将IOC容器中的service对象设置给controller层定义的field上
        doIoc();

        // 4.建立path与method的映射关系
        handlerMapping();
        System.out.println("Controller层的path和方法映射.........");
        for (Map.Entry<String, Object> map : handlerMap.entrySet()) {
            System.out.println("key:" + map.getKey() + "; value:" + map.getValue());
        }
    }

    private void doScanPackage(String basePackage) {
        URL resource = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
        String fileStr = resource.getFile();
//		System.out.println(fileStr);
        File file = new File(fileStr);
        String[] listFiles = file.list();
        for (String path : listFiles) {
            File filePath = new File(fileStr + path);
            // 如果当前是目录，则递归
            if (filePath.isDirectory()) {
                doScanPackage(basePackage + "." + path);
                // 如果是文件，则直接添加到classNames
            } else {
                classNames.add(basePackage + "." + filePath.getName());
            }
        }
    }

    // 通过存储的classnames的类字符串来反射实例化对象，并存储与beans的Map中
    // com.ygsoft.custom.annotation.CustomController.class =>com.ygsoft.custom.annotation.CustomController
    private void doInstance() {
        if (classNames.isEmpty()) {
            System.out.println("doScanner Fail....");
        }

        // 开始实例化对象,通过反射来实现
        for (String className : classNames) {
            String cn = className.replaceAll(".class", "");
            try {
                Class<?> clazz = Class.forName(cn);
                // 判断当前类是否有注解CustomController类，获取设置的CustomeRequestMapping的值
                if (clazz.isAnnotationPresent(MyController.class)) {
                    // 通过CustomeRequestMapping获取值，作为beans的key
                    MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                    String key = requestMapping.value();
                    // beans的vaue为实例化对象
                    Object value = clazz.newInstance();
                    beans.put(clazz.getSimpleName(), value);
                    // 判断当前的类是否有注解CustomService（考虑Service层），获取值
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    // 通过CustomService获取值，作为beans的key
                    MyService service = clazz.getAnnotation(MyService.class);
                    String key = service.value();
                    // beans的vaue为实例化对象



                    Class[] interfaces = clazz.getInterfaces();//获得Dog所实现的所有接口
                    Object value = clazz.newInstance();
                    for (Class inte : interfaces) {//打印
                        System.out.println(inte.getSimpleName()+"------------");
                        beans.put(inte.getSimpleName(), value);
                    }
                } else {
                    continue;
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void doIoc() {
        if (beans.isEmpty()) {
            System.out.println("no class is instance......");
            return;
        }
        for (Map.Entry<String, Object> map : beans.entrySet()) {
            // 获取实例
            Object instance = map.getValue();

            // 获取类
            Class<?> clazz = instance.getClass();
            // 如果当前是CustomController类，则获取类中定义的field来设置其对象
            if (clazz.isAnnotationPresent(MyController.class)) {
                // 获取全部的成员变量
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    // 如果当前的成员变量使用注解CustomRequestMapping进行处理
                    if (field.isAnnotationPresent(MyAutowired.class)) {
                        // 获取当前成员变量的注解值
                        MyAutowired qualifier = field.getAnnotation(MyAutowired.class);
                        String value = qualifier.value();

                        // 由于此类成员变量设置为private，需要强行设置
                        field.setAccessible(true);
                        // 将beans的实例化对象赋值给当前的变量
                        try {
                            String typeName = field.getGenericType().getTypeName();
                            field.set(instance, beans.get(typeName.substring(typeName.lastIndexOf(".") + 1)));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    private void handlerMapping() {
        if (beans.isEmpty()) {
            System.out.println("no class is instance......");
            return;
        }
        for (Map.Entry<String, Object> map : beans.entrySet()) {
            // 获取当前的对象
            Object instance = map.getValue();

            // 获取当前的类
            Class<?> clazz = instance.getClass();
            // 获取注解当前为Controller的类
            if (clazz.isAnnotationPresent(MyController.class)) {
                // 获取类上的路径
                MyRequestMapping clazzRm = clazz.getAnnotation(MyRequestMapping.class);
                String clazzPath = clazzRm.value();

                // 处理方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    // 判断注解为RequestMapping
                    if (method.isAnnotationPresent(MyRequestMapping.class)) {
                        // 获取方法上的路径
                        MyRequestMapping methodRm = method.getAnnotation(MyRequestMapping.class);
                        String methodPath = methodRm.value();
                        // 将类上的路径+方法上的路径 设置为key，方法设置为value
                        handlerMap.put(clazzPath + methodPath, method);
                        handlerMap.put(method.getName(), beans.get(clazz.getSimpleName()));
                    } else {
                        continue;
                    }
                }
            } else {
                continue;
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("doGet()............");
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 通过反射来实现方法的调用
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        System.out.println("destroy()............");
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (handlerMap.isEmpty()) {
            return;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();

        url = url.replace(contextPath, "").replaceAll("/+", "/");

        if (!this.handlerMap.containsKey(url)) {
            resp.getWriter().write("404 NOT FOUND!");
            return;
        }
        Method method = (Method) this.handlerMap.get(url);
        Object[] paramValues = getParams(req, resp);

        // 处理参数
//        HandlerAdapterService ha = (HandlerAdapterService) beans.get("HandlerAdapterService");
//        Object[] paramValues = ha.handle(req, resp, method, beans);
        //利用反射机制来调用
        try {
            method.invoke(handlerMap.get(method.getName()), paramValues);//第一个参数是method所对应的实例 在ioc容器中
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object[] getParams(HttpServletRequest req, HttpServletResponse resp) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        Method method = (Method) this.handlerMap.get(url);

        //获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //获取请求的参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        //保存参数值
        Object[] paramValues = new Object[parameterTypes.length];
        //方法的参数列表
        for (int i = 0; i < parameterTypes.length; i++) {
            //根据参数名称，做某些处理
            String requestParam = parameterTypes[i].getSimpleName();
            if (requestParam.equals("HttpServletRequest")) {
                //参数类型已明确，这边强转类型
                paramValues[i] = req;
                continue;
            }
            if (requestParam.equals("HttpServletResponse")) {
                resp.setContentType("text/html;charset=UTF-8");
                paramValues[i] = resp;
                continue;
            }
            if (requestParam.equals("String")) {
                for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
        }
        return paramValues;
    }
}
