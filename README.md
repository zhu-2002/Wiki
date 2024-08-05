# 知识库项目开发

> @author	Zenos
>
> 行百里者半九十

## 后端环境配置

### 初始化springboot项目

### 配置git地址

### 配置log显示

在resource目录下新增logback-spring.xml文件即可完成新日志 的替换。

### 开发controller接口

### 自定义配置文件

使用yml文件进行springboot的配置

### 集成通用返回类

- CommonResp

  ```java
  @Getter	
  @Setter
  public class CommonResp<T> {
  
      /**
       * 业务上的成功或失败
       */
      private boolean success = true;
  
      /**
       * 返回信息
       */
      private String message;
  
      /**
       * 返回泛型数据，自定义类型
       */
      private T content;
      
      @Override
      public String toString() {
          final StringBuffer sb = new StringBuffer("ResponseDto{");
          sb.append("success=").append(success);
          sb.append(", message='").append(message).append('\'');
          sb.append(", content=").append(content);
          sb.append('}');
          return sb.toString();
      }
  }
  ```

### 集成持久层

1. 引入依赖

   ```xml
   <!-- mysql 驱动 -->
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
       <version>8.0.33</version>
   </dependency>
   <!-- mybatis-plus -->
   <dependency>
       <groupId>com.baomidou</groupId>
       <artifactId>mybatis-plus-boot-starter</artifactId>
       <version>3.3.2</version>
   </dependency>
   <dependency>
       <groupId>com.baomidou</groupId>
       <artifactId>mybatis-plus-generator</artifactId>
       <version>3.3.2</version>
   </dependency>
   ```

2. 创建mapper资源文件

   在resource目录下创建mapper目录创建对应的TestMapper.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.java.wiki.mapper.TestMapper">
       <select id="list" resultType="com.java.wiki.domain.Test">
           select `id` from `test`
       </select>
   </mapper>
   ```

3. 构建持久层代码

    - domain

      ```java
      @Data
      public class Test {
          private Integer id ;
      }
      ```

    - mapper

      ```java
      @Repository
      public interface TestMapper {
          public List<Test> list();
      }
      ```

    - service

        - impl

          ```java
          @Service
          public class TestServiceImpl implements TestService {
          
              @Autowired
              private TestMapper testMapper ;
              @Override
              public List<Test> list(){
                  return testMapper.list();
              }
          }
          ```

      ```java
      public interface TestService {
      
          public List<Test> list() ;
      }
      ```

    - controller

      ```java
      @Autowired
      private TestService testService;
      
      @GetMapping("/test")
      public List<Test> list(){
          return testService.list() ;
      }
      ```

4. 启动类添加mapper扫描路径

   ```java
   @MapperScan("com.java.wiki.mapper")
   ```

5. application添加mapper扫描路径

   ```properties
   mybatis.mapper-locations=classpath:/mapper/**/*.xml
   ```

### 跨域请求配置

#### 在config目录下新增CorsConfig类

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders(CorsConfiguration.ALL)
                .allowedMethods(CorsConfiguration.ALL)
                .allowCredentials(true)
                .maxAge(3600); // 1小时内不需要再预检（发OPTIONS请求）
    }
}
```

#### 使用注解方式

修改Basecontroller,所有Controller继承该类

```java
@RestController
@CrossOrigin
public class BaseController {
    /**
     * /hello
     * @return "hello"
     */
    @GetMapping("/hello")
    public String hello()
    {
        return "hello ! " ;
    }


}
```

### SpringBoot过滤器

新增filter层，添加LogFilter.java

```java
@Component
public class LogFilter implements Filter {
     private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);

     @Override
     public void init(FilterConfig filterConfig) throws ServletException {

     }

     @Override
     public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
         // 打印请求信息
         HttpServletRequest request = (HttpServletRequest) servletRequest;
         LOG.info("------------- LogFilter 开始 -------------");
         LOG.info("请求地址: {} {}", request.getRequestURL().toString(), request.getMethod());
         LOG.info("远程地址: {}", request.getRemoteAddr());

         long startTime = System.currentTimeMillis();
         filterChain.doFilter(servletRequest, servletResponse);
         LOG.info("------------- LogFilter 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
     }
 }
```

### SpringBoot拦截器

- 定义登录校验拦截器MyLogInterceptor

  ```java
  /**
    * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印 /login
    */
   @Component
   public class MyLogInterceptor implements HandlerInterceptor {
  
       private static final Logger LOG = LoggerFactory.getLogger(MyLogInterceptor.class);
  
       @Override
       public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
           // 打印请求信息
           LOG.info("------------- LogInterceptor 开始 -------------");
           LOG.info("请求地址: {} {}", request.getRequestURL().toString(), request.getMethod());
           LOG.info("远程地址: {}", request.getRemoteAddr());
  
           long startTime = System.currentTimeMillis();
           request.setAttribute("requestStartTime", startTime);
           return true;
       }
  
       @Override
       public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
           long startTime = (Long) request.getAttribute("requestStartTime");
           LOG.info("------------- LogInterceptor 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
       }
   }
  ```

- 注册拦截器

  在config层新增拦截器配置中心InterceptorConfig.java

  ```java
  @Configuration
  public class InterceptorConfig implements WebMvcConfigurer {
  
      @Bean
      public MyLogInterceptor logInterceptor (){
          return new MyLogInterceptor();
      }
  
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
          registry.addInterceptor(logInterceptor())
                  .addPathPatterns("/**")
                  .excludePathPatterns("/login");
      }
  
  }
  ```

### SpringBootAOP

1. pom中添加对应的依赖

   ```xml
   <!-- aop -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-aop</artifactId>
   </dependency>
   ```

2. 新增aspect层

   添加对应的LogAspect.java

   ```java
   @Aspect
   @Component
   public class LogAspect {
   
       private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);
   
       /** 定义一个切点 */
       @Pointcut("execution(public * com.java.*.controller..*Controller.*(..))")
       public void controllerPointcut() {}
   
       @Resource
       private SnowFlake snowFlake;
   
       @Before("controllerPointcut()")
       public void doBefore(JoinPoint joinPoint) throws Throwable {
   
           // 增加日志流水号
           MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
   
           // 开始打印请求日志
           ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
           HttpServletRequest request = attributes.getRequest();
           Signature signature = joinPoint.getSignature();
           String name = signature.getName();
   
           // 打印请求信息
           LOG.info("------------- 开始 -------------");
           LOG.info("请求地址: {} {}", request.getRequestURL().toString(), request.getMethod());
           LOG.info("类名方法: {}.{}", signature.getDeclaringTypeName(), name);
           LOG.info("远程地址: {}", request.getRemoteAddr());
   
           RequestContext.setRemoteAddr(getRemoteIp(request));
   
           // 打印请求参数
           Object[] args = joinPoint.getArgs();
   		// LOG.info("请求参数: {}", JSONObject.toJSONString(args));
   
   		Object[] arguments  = new Object[args.length];
           for (int i = 0; i < args.length; i++) {
               if (args[i] instanceof ServletRequest
                       || args[i] instanceof ServletResponse
                       || args[i] instanceof MultipartFile) {
                   continue;
               }
               arguments[i] = args[i];
           }
           // 排除字段，敏感字段或太长的字段不显示
           String[] excludeProperties = {"password", "file"};
           PropertyPreFilters filters = new PropertyPreFilters();
           PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
           excludefilter.addExcludes(excludeProperties);
           LOG.info("请求参数: {}", JSONObject.toJSONString(arguments, excludefilter));
       }
   
       @Around("controllerPointcut()")
       public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
           long startTime = System.currentTimeMillis();
           Object result = proceedingJoinPoint.proceed();
           // 排除字段，敏感字段或太长的字段不显示
           String[] excludeProperties = {"password", "file"};
           PropertyPreFilters filters = new PropertyPreFilters();
           PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
           excludefilter.addExcludes(excludeProperties);
           LOG.info("返回结果: {}", JSONObject.toJSONString(result, excludefilter));
           LOG.info("------------- 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
           return result;
       }
   
       /**
        * 使用nginx做反向代理，需要用该方法才能取到真实的远程IP
        * @param request
        * @return
        */
       public String getRemoteIp(HttpServletRequest request) {
           String ip = request.getHeader("x-forwarded-for");
           if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
               ip = request.getHeader("Proxy-Client-IP");
           }
           if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
               ip = request.getHeader("WL-Proxy-Client-IP");
           }
           if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
               ip = request.getRemoteAddr();
           }
           return ip;
       }
   
   }
   ```

## 前端环境配置

### VUE3

- 项目配置

  ```shell
  > npm install -g @vue/cli@latest
  ```

- 项目启动

  ```shell
  > vue-cli-service serve
  ```

### 集成Ant Design Vue

```shell
> npm install ant-design-vue@2.2.8 --save
```

- main.ts

  ```ts
  import { createApp } from 'vue'
  import App from './App.vue'
  import router from './router'
  import store from './store'
  // 新增下面两条依赖
  import Antd from 'ant-design-vue'
  import 'ant-design-vue/dist/antd.css'
  
  // 新增一个use选项(Antd)
  createApp(App).use(store).use(router).use(Antd).mount('#app');
  ```

- Home.vue

  对应官网组件中的源码进行复制粘贴。[Ant Design Vue (antdv.com)](https://2x.antdv.com/components/button-cn)

  ```vue
  <a-button>Default Button</a-button>
  ```

### 网站首页布局开发

1. 选择布局

   在官方网站选择需要使用的布局[Ant Design Vue (antdv.com)](https://2x.antdv.com/components/layout-cn)

2. 将相关布局内容复制到项目中

   通用布局放在App.vue里面，需要替换的内容使用<router-view/>标签来代替

   ```vue
   <template>
     <a-layout>
        	......
        <router-view/>
   		......
     </a-layout>
   </template>
   ```

   在对应的路由页面中添加对应的变化标签

   Home.vue <a-layout/>

   ```vue
   <template>
     <a-layout>
    	......
     </a-layout>
   </template>
   ```

3. 将对应的样式复制到App.vue中

   ```vue
   <style>
    ......
   </style>
   ```

4. 在.eslintrc.js关闭'vue/no-unused-components' 校验

   ```js
   rules: {
   	'vue/no-unused-components' :'off'
   }
   ```

### Vue自定义组件

1. 将header和footer提取成公用组件

   在component目录下新建DefaultHeader.vue

    - 将对应的内容复制到<template>中
    - 设置暴露名称

   ```vue
   <template>
     <a-layout-header class="header">
         ........
     </a-layout-header>
   </template>
   
   <script lang="ts">
   import { defineComponent } from 'vue';
   
   export default defineComponent({
     name: 'default-header',
   });
   </script>
   ```

2. 在App.vue中调用对应的组件

    - 在<script>标签中调用
    - 在<template>中调用已经封装完成的标签

   ```vue
   <template>
     <a-layout>
       <default-header/>
        ......
     </a-layout>
   </template>
   
   <script lang="ts">
     import { defineComponent } from 'vue';
     import DefaultHeader from '@/components/DefaultHeader.vue';
   
     export default defineComponent({
       name: 'app',
       components: {
         DefaultHeader,
       },
     });
   </script>
   ```

### 		集成Http库Axios

1. 安装

   ```shell
   > npm install axios@0.21.0 --save
   ```

2. 调用

    - import
    - 编写setup方法，并调用axios.get

   ```vue
   <script lang="ts">
   import { defineComponent } from 'vue';
   import axios from 'axios' ;
   
   export default defineComponent({
     name: 'Home',
     setup(){
       console.log("setup");
       axios.get("http://127.0.0.1:8088/ebook/test")
           .then((function (resp) {
             console.log(resp)
           }))
     }
   });
   </script>
   ```

### 使用ref实现数据绑定

1. 在onMounted方法中实现相关调用

    - 导入ref
    - 定义变量
    - 编写方法
    - 返回数据

   ```vue
   <script lang="ts">
   import { defineComponent,onMounted, ref } from 'vue';
   import axios from 'axios' ;
   
   export default defineComponent({
     name: 'Home',
     setup(){
       console.log("setup");
       // 定义响应式变量
       const ebooks = ref() ;
   
       onMounted(() => {
         console.log("onMounted");
         axios.get("http://127.0.0.1:8088/ebook/test").then((function (resp) {
           const data = resp.data ;
           ebooks.value = data.content ;
         }));
       });
       return{
         ebooks
       }
     }
   });
       
   </script>
   ```

2. 新增数据标签

   ```vue
   <template>
     <a-layout>
   	......
       <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
         <pre>
   {{ebooks}}
         </pre>
       </a-layout-content>
         ......
     </a-layout>
   </template>
   ```

### 加入Ant Design Vue list组件

1. 在官网找到对应组件的网址

   [Ant Design Vue (antdv.com)](https://2x.antdv.com/components/list-cn)

2. 在页面中导入相关的列表组件<a-list>

   ```vue
   <template>
     <a-layout>
         ......
       <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
         <a-list item-layout="vertical" size="large" :pagination="pagination" :data-source="listData">
           ......
         </a-list>
       </a-layout-content>
     </a-layout>
   </template>
   ```

3. 在页面<srcript>标签中生成数据

   ```vue
   const listData: Record<string, string>[] = [];
   for (let i = 0; i < 23; i++) {
     listData.push({
       href: 'https://www.antdv.com/',
       title: `ant design vue part ${i}`,
       avatar: 'https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png',
       description:
         'Ant Design, a design language for background applications, is refined by Ant UED Team.',
       content:
         'We supply a series of design principles, practical patterns and high quality design resources (Sketch and Axure), to help people create their product prototypes beautifully and efficiently.',
     });
   }
   ```

4. 在页面<srcript>对应的setup方法中编写对应变量

   ```vue
   setup(){
       const pagination = {
         onChange: (page: number) => {
           console.log(page);
         },
         pageSize: 3,
       };
       const actions: Record<string, string>[] = [
         { type: 'StarOutlined', text: '156' },
         { type: 'LikeOutlined', text: '156' },
         { type: 'MessageOutlined', text: '2' },
       ];
        ......
       return{
          ......
         ,listData
         ,pagination
         ,actions
       }
     }
   });
   ```

### 显示图标

1. 下载

   ```shell
   > npm install --save @ant-design/icons-vue
   ```

2. 修改main.ts

   ```ts
   import * as Icons from '@ant-design/icons-vue'
   
   const app = createApp(App) ;
   app.use(store).use(router).use(Antd).mount('#app');
   
   // 全局使用图标
   const icons : any = Icons ;
   for (const i in icons){
       app.component(i,icons[i]) ;
   }
   ```

### 多环境配置

1. 在根目录下创建.env.dev.txt

   ```tex
   NODE_ENV=development
   VUE_APP_SERVER=http://127.0.0.1:8888
   VUE_APP_WS_SERVER=ws://127.0.0.1:8888
   ```

2. 在package.json中配置新的script脚本

   ```json
     "scripts": {
       "serve-dev": "vue-cli-service serve --mode dev --port 8084",
       "build-dev": "vue-cli-service build --mode dev",
     },
   ```

3. 调用相关属性

   ```java
   // 调用NODE_ENV属性
   process.env.NODE_ENV
   ```

### 使用Axios封装baseURL

1. 在main.ts中引入axios库

   ```ts
   import axios from 'axios'
   ```

2. 定义baseURL变量

   ```ts
   axios.defaults.baseURL = process.env.VUE_APP_SERVER ;
   ```

3. 修改路由

   新的路由不需要在加任何前缀

   ```js
   axios.get("/ebook/test").then((function (resp) {
   const data = resp.data ;
   ebooks.value = data.content ;
   console.log(resp) ;
   }));
   ```

### 使用Axios拦截器打印前端日志

1. 在main.ts中添加相关代码

   ```ts
   /**
    * axios拦截器
    */
   axios.interceptors.request.use(function (config) {
       console.log('请求参数：', config);
       return config;
   }, error => {
       return Promise.reject(error);
   });
   axios.interceptors.response.use(function (response) {
       console.log('返回结果：', response);
       return response;
   }, error => {
       console.log('返回错误：', error);
       return Promise.reject(error);
   });
   ```

## 业务开发

### 电子书管理功能

1. 建表

   ```sql
   drop table if exists `ebook`;
   create table `ebook` (
     `id` bigint not null comment 'id',
     `name` varchar(50) comment '名称',
     `category1_id` bigint comment '分类1',
     `category2_id` bigint comment '分类2',
     `description` varchar(200) comment '描述',
     `cover` varchar(200) comment '封面',
     `doc_count` int not null default 0 comment '文档数',
     `view_count` int not null default 0 comment '阅读数',
     `vote_count` int not null default 0 comment '点赞数',
     primary key (`id`)
   ) engine=innodb default charset=utf8mb4 comment='电子书';
   
   insert into `ebook` (id, name, description) values (1, 'Spring Boot 入门教程', '零基础入门 Java 开发，企业级应用开发最佳首选框架');
   insert into `ebook` (id, name, description) values (2, 'Vue 入门教程', '零基础入门 Vue 开发，企业级应用开发最佳首选框架');
   insert into `ebook` (id, name, description) values (3, 'Python 入门教程', '零基础入门 Python 开发，企业级应用开发最佳首选框架');
   insert into `ebook` (id, name, description) values (4, 'Mysql 入门教程', '零基础入门 Mysql 开发，企业级应用开发最佳首选框架');
   insert into `ebook` (id, name, description) values (5, 'Oracle 入门教程', '零基础入门 Oracle 开发，企业级应用开发最佳首选框架');
   ```

2. 使用MybatisX生成持久层代码

3. 新增EbookController方法

   ```java
   @RestController
   @RequestMapping("ebook")
   public class EbookController {
   
    @GetMapping("/test")
       public CommonResp list(){
           CommonResp<List<Ebook>> resp = new CommonResp<>();
           List<Ebook> ebookList = ebookService.list() ;
           resp.setContent(ebookList);
           return resp;
       }
   }
   ```

#### 页面开发

##### 新增页面

新增admin层，添加对应的页面AdminEbook.vue

```vue
<template>
  <div class="about">
    <h1>电子书管理</h1>
  </div>
</template>
```

##### 新增路由

在router层的index.ts中添加路由

```ts
,
  {
    path: '/admin/ebook',
    name: 'AdminEbook',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/admin/AdminEbook.vue')
  }
D:/workspace/vue3_workspace/wiki_web/public/image/cover2.png
```

#### 电子书管理表格开发

- 修改页面样式（html）
- 配置变量，调用接口并返回前端对应数据

```vue
<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>
              <a-button type="danger">
                删除
              </a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent,onMounted, ref } from 'vue';
import axios from 'axios' ;

export default defineComponent({
  name: 'AdminEbook',
  setup(){
    // 定义响应式变量
    const ebooks = ref() ;
    const pagination = ref({
      current: 1,
      pageSize: 1,
      total: 0
    });
    const loading = ref(false);
    const columns = [
      {
        title: '封面',
        dataIndex: 'cover',
        slots: { customRender: 'cover' }
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '分类',
        slots: { customRender: 'category' }
      },
      {
        title: '文档数',
        dataIndex: 'docCount'
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount'
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount'
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];
    
    /**
     * 数据查询
     */
    const handleQuery = (params: any) => {
      loading.value = true;
      axios.get("/ebook/test", params).then((response) => {
        loading.value = false;
        const data = response.data;
        ebooks.value = data.content ;
        // 重置分页按钮
        pagination.value.current = params.page ;
      });
    };

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    onMounted(() => {
      handleQuery({});
    });
    return{
      ebooks
      ,pagination
      ,columns
      ,loading
      ,handleTableChange
    }
  }
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
```

#### 使用PageHelper实现后端分页

1. 引入依赖

   ```xml
   <!-- pagehelper -->
   <dependency>
       <groupId>com.github.pagehelper</groupId>
       <artifactId>pagehelper-spring-boot-starter</artifactId>
       <version>1.2.13</version>
   </dependency>
   ```

2. 在对应的controller方法中需要分库分表的语句前添加pageHelper的配置

   ```java
   /**
   * param1 起始页
   * param2 页大小lahuh
   */
   PageHelper.startPage(1,10);
   ```

#### 封装分页请求参数和返回参数

- PageReq.java

  ```java
  @Getter
  @Setter
  public class PageReq {
      private int page;
  
      private int size;
  
      @Override
      public String toString() {
          final StringBuffer sb = new StringBuffer("PageReq{");
          sb.append("page=").append(page);
          sb.append(", size=").append(size);
          sb.append('}');
          return sb.toString();
      }
  }
  ```

- PageResp.java

  ```java
  @Getter
  @Setter
  public class PageResp<T> {
      private long total;
  
      private List<T> list;
  
      @Override
      public String toString() {
          final StringBuffer sb = new StringBuffer("PageResp{");
          sb.append("total=").append(total);
          sb.append(", list=").append(list);
          sb.append('}');
          return sb.toString();
      }
  }
  ```

#### 前后端联调

看日志，找bug

#### 制作电子书表单

- 点击编辑按钮，弹出编辑框，显示电子书表单

  在AdminEbook.vue中添加对应的标签，方法

#### 增加电子书编辑保存功能

##### 前端

在对应的AdminEbook页面中新增对应的请求方法

```vue
axios.post("/ebook/save", ebook.value).then((response) => {
const data = response.data; // data = commonResp
if (data.success) {
  modalVisible.value = false;
  modalLoading.value = false;

  // 重新加载列表
  handleQuery({
    page: pagination.value.current,
    size: pagination.value.pageSize,
  });
}
});
```

##### 后端

1. 在EbookController中新增/save路由，编写对应方法

   > post请求，如果是json数据需要添加@RequestBody注解，如果是form表单数据则不需要

   ```java
       @PostMapping("/save")
       public CommonResp save(@RequestBody EbookSaveReq req) {
           CommonResp resp = new CommonResp<>();
           ebookService.save(req);
           return resp;
       }
   ```

2. 在对应的service中完善对应的save方法

   ```java
   @Override
   public void save(EbookSaveReq req) {
       Ebook ebook = CopyUtil.copy(req, Ebook.class);
       if (req.getId() == null){
           //新增
           ebookMapper.insert(ebook);
       }else {
           //更新
           ebookMapper.updateById(ebook);
       }
   }
   ```

#### 电子书新增功能

##### 前端

复制edit表单的样式和函数即可

##### 后端

使用雪花算法生成其id

```java
@Component
public class SnowFlake {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1609459200000L; // 2021-01-01 00:00:00

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数
    private final static long DATACENTER_BIT = 5;//数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId = 1;  //数据中心
    private long machineId = 1;     //机器标识
    private long sequence = 0L; //序列号
    private long lastStmp = -1L;//上一次时间戳

    public SnowFlake() {
    }

    public SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }
}

```

#### 删除电子书功能

##### 前端

新增样式

```vue
<a-popconfirm
    title="删除后数据不可恢复，确认删除?"
    ok-text="是"
    cancel-text="否"
    @confirm="remove(record.id)"
>
  <a-button type="danger">
    删除
  </a-button>
</a-popconfirm>
```

新增函数

```js
const remove = (id: number) => {
  axios.delete("/ebook/del/"+id).then((response) => {
    const data = response.data; // data = commonResp
    if (data.success) {
      // 重新加载列表
      handleQuery({
        page: pagination.value.current,
        size: pagination.value.pageSize,
      });
    }
  });
};
```

##### 后端

添加新的路由

```java
@DeleteMapping("/del/{id}")
public CommonResp delete(@PathVariable Long id) {
    CommonResp resp = new CommonResp<>();
    ebookService.removeById(id);
    return resp;
}
```

#### 集成validation

##### 新增依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

##### 在类中添加注解

```java
@NotNull(message = "【页码】不能为空")
private int page;
```

##### 在对应路由方法中添加对应的注解

是添加对应的注解类生效

```java
public CommonResp list(@Valid EbookQueryReq req)
```

### 分类管理功能开发

#### 分类表设计与持久层代码生成

1. sql

   ```sql
   # 分类
   drop table if exists `category`;
   create table `category` (
     `id` bigint not null comment 'id',
     `parent` bigint not null default 0 comment '父id',
     `name` varchar(50) not null comment '名称',
     `sort` int comment '顺序',
     primary key (`id`)
   ) engine=innodb default charset=utf8mb4 comment='分类';
   
   insert into `category` (id, parent, name, sort) values (100, 000, '前端开发', 100);
   insert into `category` (id, parent, name, sort) values (101, 100, 'Vue', 101);
   insert into `category` (id, parent, name, sort) values (102, 100, 'HTML & CSS', 102);
   insert into `category` (id, parent, name, sort) values (200, 000, 'Java', 200);
   insert into `category` (id, parent, name, sort) values (201, 200, '基础应用', 201);
   insert into `category` (id, parent, name, sort) values (202, 200, '框架应用', 202);
   insert into `category` (id, parent, name, sort) values (300, 000, 'Python', 300);
   insert into `category` (id, parent, name, sort) values (301, 300, '基础应用', 301);
   insert into `category` (id, parent, name, sort) values (302, 300, '进阶方向应用', 302);
   insert into `category` (id, parent, name, sort) values (400, 000, '数据库', 400);
   insert into `category` (id, parent, name, sort) values (401, 400, 'MySQL', 401);
   insert into `category` (id, parent, name, sort) values (500, 000, '其它', 500);
   insert into `category` (id, parent, name, sort) values (501, 500, '服务器', 501);
   insert into `category` (id, parent, name, sort) values (502, 500, '开发工具', 502);
   insert into `category` (id, parent, name, sort) values (503, 500, '热门服务端语言', 503);
   ```

2. 使用MybatisX自动生成代码

#### 分类管理增删改查功能

##### 前端

1. 复制AdminEbook.vue

   使用ctl+r进行关键字替换

2. 在router层的index.ts注册路由

   ```ts
     {
       path: '/admin/category',
       name: 'AdminCategory',
       // route level code-splitting
       // this generates a separate chunk (about.[hash].js) for this route
       // which is lazy-loaded when the route is visited.
       component: () => import( '../views/admin/AdminCategory.vue')
     }
   ```

3. 在页面头部标签组件中新增对应的选项标签

   ```vue
   <a-menu-item key="admin/category">
   	<router-link to="/admin/category">分类管理</router-link>
   </a-menu-item>
   ```


##### 后端

复制Controller和Service层代码，进行关键字替换

#### 分类管理取消分页操作

##### 前端

去掉AdminCategory.vue中有关分页的相关操作

##### 后端

去掉后端Service层中有关分页的相关代码

#### 分类管理使用二级分类查询

通过查询categoryId2分类显示对应电子书目录

##### 前端

设计对应样式，返回请求参数新增CategoryId2字段

##### 后端

service层添加对应代码

```java
if (!ObjectUtils.isEmpty(req.getCategoryId2())) {
    wrapper.eq("category2_id",req.getCategoryId2());
}
```

### 文档功能开发

#### 文档表设计与持久层代码生成

```sql
-- 文档表
drop table if exists `doc`;
create table `doc` (
  `id` bigint not null comment 'id',
  `ebook_id` bigint not null default 0 comment '电子书id',
  `parent` bigint not null default 0 comment '父id',
  `name` varchar(50) not null comment '名称',
  `sort` int comment '顺序',
  `view_count` int default 0 comment '阅读数',
  `vote_count` int default 0 comment '点赞数',
  primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='文档';
```

#### 增删改查

同上一节的分类管理操作，复制category的相关类，区别是该功能能显示路由在AdminEbook中

#### 使用树形选择器查询二级以下目录

见前端代码6-3，并修复了分类和文档管理的查询显示的相关bug

#### Vue页面传递新增默认ebookId参数

见前端代码6-4

#### 递归删除文档

##### 前端

> 见前端代码6-5

将传参类型修改为字符串类型，对目标节点进行递归遍历，拿到所有需要删除节点的列表数据，封装成请求参数传递给后端

##### 后端

修改controller中的del路由方法

```java
// 路由修改为字符串格式
@DeleteMapping("/del/{idStr}")
public CommonResp delete(@PathVariable String idStr) {
    CommonResp resp = new CommonResp<>();
    // 使用列表删除的方法
    docService.removeByIds(Arrays.asList(idStr.split(",")));
    return resp;
}
```

#### 前端集成富文本编辑器WangEditor

详细见前端代码6-6

#### 文档内容表设计与代码生成

```sql
-- 文档内容
drop table if exists `content`;
create table `content` (
  `id` bigint not null comment '文档id',
  `content` mediumtext not null comment '内容',
  primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='文档内容';
```

#### 文档内容保存功能

##### 前端

在AdminDoc.vue中添加文本编辑变量，保存到doc.value.content，与后端新建字段content一致，返回给后端。

##### 后端

1. 修改DocSaveReq

   添加新的字段content用于保存前端传来的编辑内容变量

2. Service层修改save函数

   ```java
   @Override
   public void save(DocSaveReq req) {
       Doc doc = CopyUtil.copy(req, Doc.class);
       Content content = CopyUtil.copy(req, Content.class);
       if (req.getId() == null){
           //新增
           doc.setId(snowFlake.nextId());
           docMapper.insert(doc);
           //新增
           content.setId(doc.getId());
           contentMapper.insert(content);
       } else if ( contentMapper.selectById(doc.getId()) == null ) {
           //更新
           docMapper.updateById(doc);
           //新增
           content.setId(doc.getId());
           contentMapper.insert(content);
       } else {
           //更新
           docMapper.updateById(doc);
           //更新
           contentMapper.updateById(content);
       }
   }
   ```

#### 文档内容读取功能

##### 前端

1. 新增处理内容查询请求的方法

   ```ts
   /**
   * 内容查询
   **/
   const handleQueryContent = () => {
    axios.get("/doc/find-content/" + doc.value.id).then((response) => {
   const data = response.data;
   if (data.success) {
    editor.txt.html(data.content)
   } else {
    message.error(data.message);
   }
    });
   }
   ```

2. 在edit函数中调用该方法

   ```ts
   const edit = (record: any) => {
   	......
     handleQueryContent();
   	......
   };
   ```

##### 后端

1. 新增路由方法

   ```java
   @GetMapping("/find-content/{id}")
   public CommonResp findContent(@PathVariable Long id) {
       CommonResp<String> resp = new CommonResp<>();
       String content = docService.findContent(id);
       resp.setContent(content);
       return resp;
   }
   ```

2. Service层新增对应方法

   ```java
   @Override
   public String findContent(Long id) {
       Content content = contentMapper.selectById(id);
       if ( content == null ){
           return "";
       }
       return content.getContent();
   }
   ```

#### 文档页面功能开发

##### 前端

1. 新增Doc.vue界面，并注册路由de等
2. 在Doc页面新增相关的数据查询路由等方法

##### 后端

1. Controller新增查询路由

   ```java
   // 查询一个电子书子目录下的所有文档，避免访问到别的电子书
   @GetMapping("/list/{ebookId}")
   public CommonResp all(@PathVariable Long ebookId) {
       CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
       List<DocQueryResp> list = docService.listByEbookId(ebookId);
       resp.setContent(list);
       return resp;
   }
   ```

2. Service层新增对应的查询方法

   ```java
   @Override
   public List<DocQueryResp> listByEbookId(Long ebookId) {
       QueryWrapper<Doc> wrapper = new QueryWrapper<>() ;
       wrapper.orderByAsc("sort");
       if (!ObjectUtils.isEmpty(ebookId)) {
           wrapper.eq("ebook_id",ebookId);
       }
       List<Doc> docList = docMapper.selectList(wrapper);
       List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
   
       return list ;
   }
   ```

##### 前端界面优化处理

详细见前端代码分支6-10

### 用户模块开发

#### 用户表设计与持久层代码生成

```sql
-- 用户表
drop table if exists `user`;
create table `user` (
  `id` bigint not null comment 'ID',
  `login_name` varchar(50) not null comment '登陆名',
  `name` varchar(50) comment '昵称',
  `password` char(32) not null comment '密码',
  primary key (`id`),
  unique key `login_name_unique` (`login_name`)
) engine=innodb default charset=utf8mb4 comment='用户';

insert into `user` (id, `login_name`, `name`, `password`) values (1, 'test', '测试', 'e70e2222a9d67c4f2eae107533359aa4');
```

##### 完成增删改查基本功能

同上面章节，复制ebook相关代码即可。

#### 增加用户登录名校验

##### 前端

新增登录名校验，不允许修改登录名，AdminUser.vue新增disabled属性

```vue
<a-form-item label="登陆名">
	<a-input v-model:value="user.loginName" :disabled="!!user.id"/>
</a-form-item>
```



##### 后端

登录名不能重复，增加自定义异常

1. 新增自定义异常

    - BusinessException

      ```java
      public class BusinessException extends RuntimeException{
      
          private BusinessExceptionCode code;
      
          public BusinessException (BusinessExceptionCode code) {
              super(code.getDesc());
              this.code = code;
          }
      
          public BusinessExceptionCode getCode() {
              return code;
          }
      
          public void setCode(BusinessExceptionCode code) {
              this.code = code;
          }
      
          /**
           * 不写入堆栈信息，提高性能
           */
          @Override
          public Throwable fillInStackTrace() {
              return this;
          }
      }
      ```

    - BusinessExceptionCode

      ```java
      public enum BusinessExceptionCode {
      
          USER_LOGIN_NAME_EXIST("登录名已存在") ;
      
          private String desc;
      
          BusinessExceptionCode(String desc) {
              this.desc = desc;
          }
      
          public String getDesc() {
              return desc;
          }
      
          public void setDesc(String desc) {
              this.desc = desc;
          }
      }
      ```

2. 通知添加异常处理函数

   ```java
   /**
    * 统一异常处理、数据预处理等
    */
   @ControllerAdvice
   public class ControllerExceptionHandler {
   
       private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);
       ......
       /**
        * 校验异常统一处理
        * @param e
        * @return
        */
       @ExceptionHandler(value = BusinessException.class)
       @ResponseBody
       public CommonResp validExceptionHandler(BusinessException e) {
           CommonResp commonResp = new CommonResp();
           LOG.warn("业务异常：{}", e.getCode().getDesc());
           commonResp.setSuccess(false);
           commonResp.setMessage(e.getCode().getDesc());
           return commonResp;
       }
   	......
   }
   ```

3. 修改Service层的新增代码

   当用户新增的时候，如果用户名冲突，返回异常

   ```java
   @Override
   public void save(UserSaveReq req) {
       User user = CopyUtil.copy(req, User.class);
       QueryWrapper<User> wrapper = new QueryWrapper<>();
       wrapper.eq("login_name",req.getLoginName());
       if (req.getId() == null){
           if ( userMapper.selectOne(wrapper) != null ){
               throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
           }
           else {
               //新增
               user.setId(snowFlake.nextId());
               userMapper.insert(user);
           }
       }else {
           //更新
           userMapper.updateById(user);
       }
   }
   ```

4. 修改Service层的更新代码

   当用户更新信息时，不能修改其登录名

   ```java
   @Override
   public void save(UserSaveReq req) {
       User user = CopyUtil.copy(req, User.class);
       QueryWrapper<User> wrapper = new QueryWrapper<>();
       wrapper.eq("login_name",req.getLoginName());
       if (req.getId() == null){
           ......
       }else {
           //更新
           user.setLoginName(null);
           userMapper.updateById(user);
       }
   }
   ```

   User字段新增LoginName属性校验

   ```java
   // 当插入数据时，如果字段的值为空，则使用数据库默认值
   @TableField(updateStrategy = FieldStrategy.NOT_EMPTY )
   private String loginName;
   ```

#### 用户密码加密处理

##### 前端

现在前端进行一次加密

1. 引入md5.js函数
2. 导入md5,js函数
3. 再AdminUser.vue中调用对应的函数方法，对对应的值进行加密

详细内容见前端代码分支《7-4 新增用户密码加密处理》

##### 后端

在后端再次进行加密，实现双重加密。

在controller层的save路由中调用自带的MD5函数库对传来的密码进行加密，再存储

```java
@PostMapping("/save")
public CommonResp save(@Valid @RequestBody UserSaveReq req) {
    req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
    CommonResp resp = new CommonResp<>();
    userService.save(req);
    return resp;
}
```

#### 用户密码重置功能

##### 前端

详细见代码分支《7-5 去掉编辑功能中的重置密码功能，新增单独的密码重置功能》

##### 后端

1. 更新Service层代码，请求参数中密码为空的时候，使得密码不更新

   ```java
   @Override
   public void save(UserSaveReq req) {
       User user = CopyUtil.copy(req, User.class);
       QueryWrapper<User> wrapper = new QueryWrapper<>();
       wrapper.eq("login_name",req.getLoginName());
       if (req.getId() == null){
           ......
       }else {
           //更新
           user.setLoginName(null);
           user.setPassword(null);
           userMapper.updateById(user);
       }
   }
   ```

2. 新增密码修改请求体

   ```java
   @Data
   public class UserResetPasswordReq {
       private Long id;
   
       @NotNull(message = "【密码】不能为空")
       // @Length(min = 6, max = 20, message = "【密码】6~20位")
       @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$", message = "【密码】至少包含 数字和英文，长度6-18")
       private String password;
   }
   
   ```

3. Service新增只修改密码的方法

   ```java
   @Override
   public void resetPassword(UserResetPasswordReq req) {
       User user = CopyUtil.copy(req, User.class);
       user.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
       userMapper.updateById(user);
   }
   ```

4. Controller新增对应的路由方法

   ```java
   @PostMapping("/reset-password")
   public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
       CommonResp resp = new CommonResp<>();
       userService.resetPassword(req);
       return resp;
   }
   ```


#### 用户登录功能

- 在service层新增login方法

  ```java
  @Override
  public UserLoginResp login(UserLoginReq req) {
      User user = CopyUtil.copy(req, User.class);
      user.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
      QueryWrapper<User> wrapper = new QueryWrapper<>();
      wrapper.eq("login_name",user.getLoginName());
      if ( userMapper.selectOne(wrapper) == null ){
          // 用户名不存在
          throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
      }
      else {
          if ( userMapper.selectOne(wrapper).getPassword().equals(user.getPassword()) ){
              // 密码正确
              UserLoginResp userLoginResp = CopyUtil.copy(userMapper.selectOne(wrapper), UserLoginResp.class);
              return userLoginResp;
          }
          else {
              // 密码错误
              throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
          }
      }
  }
  ```

- Controller新增路由方法

  ```java
  @PostMapping("/login")
  public CommonResp login(@Valid @RequestBody UserLoginReq req) {
      CommonResp<UserLoginResp> resp = new CommonResp<>();
      UserLoginResp userLoginResp = userService.login(req);
      resp.setContent(userLoginResp);
      return resp;
  }
  ```

#### 集成redis，存放token

1. 导入依赖

   ```xml
   <!--整合redis-->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   ```

2. application添加数据库配置

   ```properties
   spring.redis.host=hadoop100
   spring.redis.port=6379
   spring.redis.password=password
   spring.redis.database=0
   ```

3. 导入RedisOperator工具类

   ```java
   @Component
   public class RedisOperator {
   	
   	@Autowired
   	private StringRedisTemplate redisTemplate;
   
   	// Key（键），简单的key-value操作
   
   	/**
   	 * 判断key是否存在
   	 * @param key
   	 * @return
   	 */
   	public boolean keyIsExist(String key) {
   		return redisTemplate.hasKey(key);
   	}
   
   	/**
   	 * 实现命令：TTL key，以秒为单位，返回给定 key的剩余生存时间(TTL, time to live)。
   	 * 
   	 * @param key
   	 * @return
   	 */
   	public long ttl(String key) {
   		return redisTemplate.getExpire(key);
   	}
   	
   	/**
   	 * 实现命令：expire 设置过期时间，单位秒
   	 * 
   	 * @param key
   	 * @return
   	 */
   	public void expire(String key, long timeout) {
   		redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
   	}
   	
   	/**
   	 * 实现命令：increment key，增加key一次
   	 * 
   	 * @param key
   	 * @return
   	 */
   	public long increment(String key, long delta) {
   		return redisTemplate.opsForValue().increment(key, delta);
   	}
   
   	/**
   	 * 累加，使用hash
   	 */
   	public long incrementHash(String name, String key, long delta) {
   		return redisTemplate.opsForHash().increment(name, key, delta);
   	}
   
   	/**
   	 * 累减，使用hash
   	 */
   	public long decrementHash(String name, String key, long delta) {
   		delta = delta * (-1);
   		return redisTemplate.opsForHash().increment(name, key, delta);
   	}
   
   	/**
   	 * hash 设置value
   	 */
   	public void setHashValue(String name, String key, String value) {
   		redisTemplate.opsForHash().put(name, key, value);
   	}
   
   	/**
   	 * hash 获得value
   	 */
   	public String getHashValue(String name, String key) {
   		return (String)redisTemplate.opsForHash().get(name, key);
   	}
   
   	/**
   	 * 实现命令：decrement key，减少key一次
   	 *
   	 * @param key
   	 * @return
   	 */
   	public long decrement(String key, long delta) {
   		return redisTemplate.opsForValue().decrement(key, delta);
   	}
   
   	/**
   	 * 实现命令：KEYS pattern，查找所有符合给定模式 pattern的 key
   	 */
   	public Set<String> keys(String pattern) {
   		return redisTemplate.keys(pattern);
   	}
   
   	/**
   	 * 实现命令：DEL key，删除一个key
   	 * 
   	 * @param key
   	 */
   	public void del(String key) {
   		redisTemplate.delete(key);
   	}
   
   	// String（字符串）
   
   	/**
   	 * 实现命令：SET key value，设置一个key-value（将字符串值 value关联到 key）
   	 * 
   	 * @param key
   	 * @param value
   	 */
   	public void set(String key, String value) {
   		redisTemplate.opsForValue().set(key, value);
   	}
   
   	/**
   	 * 实现命令：SET key value EX seconds，设置key-value和超时时间（秒）
   	 * 
   	 * @param key
   	 * @param value
   	 * @param timeout
   	 *            （以秒为单位）
   	 */
   	public void set(String key, String value, long timeout) {
   		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
   	}
   
   	/**
   	 * 如果key不存在，则设置，如果存在，则报错
   	 * @param key
   	 * @param value
   	 */
   	public void setnx60s(String key, String value) {
   		redisTemplate.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
   	}
   
   	/**
   	 * 如果key不存在，则设置，如果存在，则报错
   	 * @param key
   	 * @param value
   	 */
   	public void setnx(String key, String value) {
   		redisTemplate.opsForValue().setIfAbsent(key, value);
   	}
   
   	/**
   	 * 实现命令：GET key，返回 key所关联的字符串值。
   	 * 
   	 * @param key
   	 * @return value
   	 */
   	public String get(String key) {
   		return (String)redisTemplate.opsForValue().get(key);
   	}
   
   	/**
   	 * 批量查询，对应mget
   	 * @param keys
   	 * @return
   	 */
   	public List<String> mget(List<String> keys) {
   		return redisTemplate.opsForValue().multiGet(keys);
   	}
   
   	/**
   	 * 批量查询，管道pipeline
   	 * @param keys
   	 * @return
   	 */
   	public List<Object> batchGet(List<String> keys) {
   
   //		nginx -> keepalive
   //		redis -> pipeline
   
   		List<Object> result = redisTemplate.executePipelined(new RedisCallback<String>() {
   			@Override
   			public String doInRedis(RedisConnection connection) throws DataAccessException {
   				StringRedisConnection src = (StringRedisConnection)connection;
   
   				for (String k : keys) {
   					src.get(k);
   				}
   				return null;
   			}
   		});
   
   		return result;
   	}
   
   
   	// Hash（哈希表）
   
   	/**
   	 * 实现命令：HSET key field value，将哈希表 key中的域 field的值设为 value
   	 * 
   	 * @param key
   	 * @param field
   	 * @param value
   	 */
   	public void hset(String key, String field, Object value) {
   		redisTemplate.opsForHash().put(key, field, value);
   	}
   
   	/**
   	 * 实现命令：HGET key field，返回哈希表 key中给定域 field的值
   	 * 
   	 * @param key
   	 * @param field
   	 * @return
   	 */
   	public String hget(String key, String field) {
   		return (String) redisTemplate.opsForHash().get(key, field);
   	}
   
   	/**
   	 * 实现命令：HDEL key field [field ...]，删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
   	 * 
   	 * @param key
   	 * @param fields
   	 */
   	public void hdel(String key, Object... fields) {
   		redisTemplate.opsForHash().delete(key, fields);
   	}
   
   	/**
   	 * 实现命令：HGETALL key，返回哈希表 key中，所有的域和值。
   	 * 
   	 * @param key
   	 * @return
   	 */
   	public Map<Object, Object> hgetall(String key) {
   		return redisTemplate.opsForHash().entries(key);
   	}
   
   	// List（列表）
   
   	/**
   	 * 实现命令：LPUSH key value，将一个值 value插入到列表 key的表头
   	 * 
   	 * @param key
   	 * @param value
   	 * @return 执行 LPUSH命令后，列表的长度。
   	 */
   	public long lpush(String key, String value) {
   		return redisTemplate.opsForList().leftPush(key, value);
   	}
   
   	/**
   	 * 实现命令：LPOP key，移除并返回列表 key的头元素。
   	 * 
   	 * @param key
   	 * @return 列表key的头元素。
   	 */
   	public String lpop(String key) {
   		return (String)redisTemplate.opsForList().leftPop(key);
   	}
   
   	/**
   	 * 实现命令：RPUSH key value，将一个值 value插入到列表 key的表尾(最右边)。
   	 * 
   	 * @param key
   	 * @param value
   	 * @return 执行 LPUSH命令后，列表的长度。
   	 */
   	public long rpush(String key, String value) {
   		return redisTemplate.opsForList().rightPush(key, value);
   	}
   
   }
   ```

4. 修改conrtoller层中对应的login路由方法

   ```
   @PostMapping("/login")
   public CommonResp login(@Valid @RequestBody UserLoginReq req) {
       CommonResp<UserLoginResp> resp = new CommonResp<>();
       UserLoginResp userLoginResp = userService.login(req);
   
       Long token = snowFlake.nextId();
       LOG.info("生成单点登录token：{}，并放入redis中", token);
       userLoginResp.setToken(token.toString());
       redis.set(REDIS_USER_TOKEN+":"+token, JSONObject.toJSONString(userLoginResp));
       resp.setContent(userLoginResp);
       return resp;
   }
   ```

#### 使用VUEX将前端变量转换成全局变量

VUEX使全局响应式变量，使用全局变量保存信息，防止刷新后内容丢失

详细见前端分支《7-6-2》、《7-7-1》

#### 用户退出功能

##### 前端

1. 在Header新增退出登录按钮，在您好，用户名标签后面

   ```vue
   <a-popconfirm
       title="确认退出登录?"
       ok-text="是"
       cancel-text="否"
       @confirm="logout()"
   >
     <a class="login-menu" v-show="user.id">
       <span>退出登录</span>
     </a>
   ```

2. 新增路由方法，并返回对应函数名

   ```ts
   // 退出登录
   const logout = () => {
     axios.get('/user/logout/' + user.value.token).then((response) => {
       const data = response.data;
       if (data.success) {
         message.success("退出登录成功！");
         store.commit("setUser", {});
       } else {
         message.error(data.message);
       }
     });
   };
   ```

##### 后端

新增logout路由方法

```java
@GetMapping("/logout/{token}")
public CommonResp logout(@PathVariable String token) {
    CommonResp resp = new CommonResp<>();
    redis.del(REDIS_USER_TOKEN+":"+token);
    LOG.info("从redis中删除token: {}", token);
    return resp;
}
```

#### 后端登录拦截器

##### 前端

在main.ts的拦截器中加入token字段

```ts
/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
    console.log('请求参数：', config);
    const token = store.state.user.token;
    if (Tool.isNotEmpty(token)) {
        config.headers.token = token;
        console.log("请求headers增加token:", token);
    }
    return config;
}, error => {
    return Promise.reject(error);
});
```

##### 后端

1. inceptor层新建拦截器

   ```java
   @Component
   public class LoginInterceptor implements HandlerInterceptor {
   
       private static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);
   
       @Autowired
       private RedisOperator redis;
   
       @Override
       public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
           // 打印请求信息
           LOG.info("------------- LoginInterceptor 开始 -------------");
           long startTime = System.currentTimeMillis();
           request.setAttribute("requestStartTime", startTime);
   
           // OPTIONS请求不做校验,
           // 前后端分离的架构, 前端会发一个OPTIONS请求先做预检, 对预检请求不做校验
           if(request.getMethod().toUpperCase().equals("OPTIONS")){
               return true;
           }
   
           String path = request.getRequestURL().toString();
           LOG.info("接口登录拦截：，path：{}", path);
   
           //获取header的token参数
           String token = request.getHeader("token");
           LOG.info("登录校验开始，token：{}", token);
           if (token == null || token.isEmpty()) {
               LOG.info( "token为空，请求被拦截" );
               response.setStatus(HttpStatus.UNAUTHORIZED.value());
               return false;
           }
           Object object = redis.get(REDIS_USER_TOKEN+":"+token);
           if (object == null) {
               LOG.warn( "token无效，请求被拦截" );
               response.setStatus(HttpStatus.UNAUTHORIZED.value());
               return false;
           } else {
               LOG.info("已登录：{}", object);
               LoginUserContext.setUser(JSON.parseObject((String) object, UserLoginResp.class));
               return true;
           }
       }
   
       @Override
       public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
           long startTime = (Long) request.getAttribute("requestStartTime");
           LOG.info("------------- LoginInterceptor 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
       }
   
       @Override
       public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
   //        LOG.info("LogInterceptor 结束");
       }
   }
   ```

2. config层注册路由

   ```java
   @Configuration
   public class SpringMvcConfig implements WebMvcConfigurer {
   
       @Autowired
       LoginInterceptor loginInterceptor;
   
       @Override
       public void addInterceptors(InterceptorRegistry registry) {
           registry.addInterceptor(loginInterceptor)
                   .addPathPatterns("/**")
                   .excludePathPatterns(
                           "/test/**",
                           "/redis/**",
                           "/user/login",
                           "/category/all",
                           "/ebook/list",
                           "/doc/all/**",
                           "/doc/find-content/**"
                   );
       }
   }
   ```

### 阅读模块开发

#### 文档阅读数更新

见前端分支8-1

#### 文档点赞功能开发

##### 前端

见前端分支8-2

##### 后端

1. 在docController层新增vote路由方法

2. 限制一个IP只能点赞一次

    - 新建ip数据库

      只有一个主键，为ip，varchar(255)，使用文档id与ip地址拼接

    - 使用MybatisX自动生成代码

    - 在DocService新增vote方法

      ```java
      @Override
      public void vote(Long id) {
          Doc doc = docMapper.selectById(id);
          String ipStr = "DOC_VOTE_" + id + "_" + RequestContext.getRemoteAddr();
          if ( ipMapper.selectById(ipStr) == null ){
              doc.setVoteCount(doc.getVoteCount() + 1);
              docMapper.updateById(doc);
      
              Ip ip = new Ip();
              ip.setId(ipStr);
              ipMapper.insert(ip);
          }
          else {
              throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
          }
      }
      ```

    - 在DocController新增vote路由方法

      ```java
      @GetMapping("/vote/{id}")
      public CommonResp vote(@PathVariable Long id) {
          CommonResp commonResp = new CommonResp();
          docService.vote(id);
          return commonResp;
      }
      ```

   #### 文档定时汇总功能

   ##### 后端

    1. 新增自定义Docmapper

        - DocMapperCustom.xml

          ```xml
          <?xml version="1.0" encoding="UTF-8"?>
          <!DOCTYPE mapper
                  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
                  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
          <mapper namespace="com.java.wiki.mapper.DocMapperCustom">
          
              <update id="updateEbookInfo">
                  update ebook t1, (select ebook_id, count(1) doc_count, sum(view_count) view_count, sum(vote_count) vote_count from doc group by ebook_id) t2
                  set t1.doc_count = t2.doc_count, t1.view_count = t2.view_count, t1.vote_count = t2.vote_count
                  where t1.id = t2.ebook_id
              </update>
          
          </mapper>
          ```

        - DocMapperCustom.java

          ```java
          package com.java.wiki.mapper;
          
          import com.baomidou.mybatisplus.core.mapper.BaseMapper;
          import com.java.wiki.domain.Doc;
          
          public interface DocMapperCustom extends BaseMapper<Doc> {
              public void updateEbookInfo();
          }
          ```

        - 在DocService层调用相关方法

          ```java
          @Override
          public void updateEbookInfo() {
              myDocMapper.updateEbookInfo();
          }
          ```

    2. 添加定时任务

       ```java
       @Component
       public class DocJob {
       
           private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);
       
           @Autowired
           private DocService docService;
       
           /**
            * 每30秒更新电子书信息
            */
           @Scheduled(cron = "5/30 * * * * ?")
           public void cron() {
               docService.updateEbookInfo();
           }
       
       }
       ```

    3. 启动项增加注解

       ```java
       @EnableScheduling
       ```









## 附录

> 对应源码：
>
> 