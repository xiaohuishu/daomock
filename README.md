DaoMock
=====================
Dao层持久化封装
1.根据项目的PO实体，VO实体来生成对应属性的XML文件信息(配上对的主外键注解，表名注解)；
2.根据XML类的属性来拼装不同操作的SQL语句(save update delete findByEntitys(单表) findBySearch(多表));
3.一个小型的IOC容器来进行baseService的注入;
