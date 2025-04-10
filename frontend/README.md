# Axios 改造为 Request 实例的总结
(在commit_id为co1e1e39c的那次提交的基础上进行的修改)
## 改造原因
- 统一管理请求配置
- 集中处理 token 认证
- 通过拦截器统一处理请求头
- 避免重复代码
- 便于后期维护

## 具体步骤

### 1. 创建请求实例
在 <mcfile name="utils/request.js" path="/Users/yq/Desktop/demo/java_web/java_web_cs2022/frontend/src/utils/request.js"></mcfile> 中：
```javascript
import axios from 'axios'

const request = axios.create()

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default request
```

### 2. 修改 Store
在 <mcfile name="stores/user.js" path="/Users/yq/Desktop/demo/java_web/java_web_cs2022/frontend/src/stores/user.js"></mcfile> 中移除手动设置 token 的代码。

### 3. 更新组件
修改所有使用 axios 的组件：
- 更新导入语句
- 替换请求方法
- 移除手动的请求头管理

## 主要改动
```diff
- import axios from 'axios'
+ import request from '@/utils/request'

- axios.get('/api/...')
+ request.get('/api/...')
```

这样的改造使得代码更加模块化，也更符合最佳实践。

# Tailwind CSS 配置过程总结

## 目标
将 Tailwind CSS 从 CDN 方式改为本地安装方式

## 原因
- CDN 方式在开发环境可能不稳定
- 本地安装可以更好地控制版本
- 生产环境性能更好
- 可以更好地与 Vite 和 PostCSS 集成

## 实施步骤
1. 移除 CDN 引用
```html:/Users/yq/Desktop/demo/java_web/java_web_cs2022/frontend/index.html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <link rel="icon" href="/favicon.ico">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <!-- 移除 CDN 引用 -->
    <title>Todolist</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

2. 安装依赖
```bash
npm install -D tailwindcss@3.4.1 postcss@8.4.33 autoprefixer@10.4.17
```

3. 配置文件设置
```javascript:/Users/yq/Desktop/demo/java_web/java_web_cs2022/frontend/postcss.config.js
export default {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
}
```

```javascript:/Users/yq/Desktop/demo/java_web/java_web_cs2022/frontend/tailwind.config.js
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

4. 创建并导入样式文件
```css:/Users/yq/Desktop/demo/java_web/java_web_cs2022/frontend/src/style.css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

```javascript:/Users/yq/Desktop/demo/java_web/java_web_cs2022/frontend/src/main.js
import './style.css'  // 确保这行在最上面
// ... 其他代码 ...
```

## 遇到的问题及解决方案

### 1. npm 权限问题
- **问题**: npm 安装时出现权限错误
- **解决**: 修复权限
```bash
sudo chown -R 501:20 "/Users/yq/.npm"
```

### 2. PostCSS 插件加载错误
- **问题**: PostCSS 无法正确加载 Tailwind 插件
- **解决**: 使用 ES modules 语法配置文件

### 3. Tailwind 版本兼容性问题
- **问题**: 不同版本的依赖包之间存在兼容性问题
- **解决**: 安装指定版本的依赖包
```bash
npm install -D tailwindcss@3.4.1 postcss@8.4.33 autoprefixer@10.4.17
```

## 最终解决方案
1. 使用正确版本的依赖包
2. 采用 ES modules 语法的配置文件
3. 确保配置文件正确设置
4. 在 main.js 中正确导入样式文件

这个过程展示了前端开发中处理依赖和配置问题的典型流程，需要特别注意版本兼容性和配置文件的正确格式。