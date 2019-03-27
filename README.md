# webguard
# 程序名:网页防篡改MD5版
# 使用方法: 
# 1.将程序jar包部署之前，在上一级目录中自建一个filePath.txt文件，文件内写入将要检测得网页文件或文件路径，以回车作为分隔符；
# 2.在程序jar包的同目录中建立config文件夹，里面需要包含application.properties配置文件
# 3.相关参数需要在application.properties中建立或修改，其中
#       mail.succeeded.to ： 网页安全所发送邮件对象
#       mail.succeeded.cc ： 网页安全所发送邮件对象
#       mail.failed.to    ： 网页已被篡改所发送邮件对象
#       mail.failed.cc    ： 网页已被篡改所发送邮件对象
#       succeedSubject    ： 网页安全报告邮件标题
#       failSubject       ： 网页被篡改报告邮件标题
#       SendTime          : 报告邮件定时发送时间
# 4.生成的MD5存储文件保存在程序同目录result.txt中
# 5.首次运行需要添加init参数，其他运行则需要删除init参数

example：
     jar包路径：C:\works\md5
     config文件路径：C:\works\md5\config
     filePath文件路径：C:\works

     第一次测试：java -jar /*你的jar包名称*/ --init
     非首次测试: java -jar /*你的jar包名称*/


