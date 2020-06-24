### 多人文件仓库
(WareHouse) 是多人文件仓库，用户自己单人上传的文件直接放在第一级界面，没有文件仓库的概念。

### 登录凭证
目前仅支持使用邮箱登录和注册
### 数据格式说明
- 用户登录密码
 ^[0-9a-zA-Z!@#$%^&*.]{6,25}$
- 昵称
 ^.{4,14}$
- Email
length = 30

### Restful Verb
GET（SELECT）：从服务器取出资源（一项或多项）。
POST（CREATE）：在服务器新建一个资源。
PUT（UPDATE）：在服务器更新资源（客户端提供改变后的完整资源）。
PATCH（UPDATE）：在服务器更新资源（客户端提供改变的属性）。
DELETE（DELETE）：从服务器删除资源。

### 部署环境要求
一下目录必须要有
/home/fileship/
/home/fileship/zip_temp/