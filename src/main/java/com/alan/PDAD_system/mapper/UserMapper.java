package com.alan.PDAD_system.mapper;

import com.alan.PDAD_system.entity.User;
import org.apache.ibatis.annotations.*;

/*@Mapper 是 Mybatis 的注解，和 Spring 没有关系。
 这个注解一般使用在Dao层接口上，相当于一个mapper.xml
 文件，它的作用就是将接口生成一个动态代理类。*/
@Mapper
public interface UserMapper {
        // 根据用户ID查询用户
        @Select("SELECT * FROM user WHERE userId = #{userId}")
        User findById(@Param("userId") String userId);




        @Update("UPDATE user SET  id_number = #{idNumber}, email = #{email} WHERE userId = #{userId}")
        int updateUser(@Param("userId") String userId,
                       @Param("idNumber") String idNumber,
                       @Param("email") String email);




        // 插入新用户到 user 表
        @Insert("INSERT INTO user (userId,password,email,id_number,role,age,gender,status) " +
                "VALUES (#{userId}, #{password}, #{email}, #{idNumber}, #{role}, #{age},#{gender}, #{status})")
        void addUser(User user);


}
