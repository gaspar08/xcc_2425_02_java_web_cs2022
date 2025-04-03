# Fetch vs Axios 对比

## 基本介绍

### Fetch
- 浏览器原生API
- 不需要额外安装依赖
- 更轻量级的解决方案

### Axios
- 基于Promise的HTTP客户端
- 需要安装额外的npm包
- 功能更丰富的解决方案

## 主要区别

### 1. 数据处理

#### Fetch
```javascript
fetch('/api/data')
  .then(response => response.json())
  .then(data => console.log(data))
```
- 需要手动调用.json()方法转换数据
- 不自动转换JSON数据

#### Axios
```javascript
axios.get('/api/data')
  .then(response => console.log(response.data))
```
- 自动转换JSON数据
- 直接通过response.data访问数据

### 2. 错误处理

#### Fetch
```javascript
fetch('/api/data')
  .then(response => {
    if (!response.ok) {
      throw new Error('网络请求失败')
    }
    return response.json()
  })
  .catch(error => console.error('错误:', error))
```
- 只有网络错误才会触发catch
- 需要手动检查response.ok

#### Axios
```javascript
axios.get('/api/data')
  .then(response => console.log(response.data))
  .catch(error => {
    if (error.response) {
      console.error('请求错误:', error.response.status)
    }
  })
```
- HTTP错误状态码会自动触发catch
- 提供更详细的错误信息

### 3. 请求配置

#### Fetch
```javascript
fetch('/api/data', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(data)
})
```
- 配置选项较基础
- 需要手动转换请求体

#### Axios
```javascript
axios({
  method: 'post',
  url: '/api/data',
  headers: { 'Content-Type': 'application/json' },
  data: data
})
```
- 提供更多配置选项
- 自动转换请求体
- 支持请求和响应拦截器

## 使用建议

### 选择Fetch的场景
- 简单的数据请求
- 追求轻量级解决方案
- 不需要复杂的功能时

### 选择Axios的场景
- 需要更完善的错误处理
- 需要请求/响应拦截器
- 需要更多配置选项
- 需要更好的浏览器兼容性