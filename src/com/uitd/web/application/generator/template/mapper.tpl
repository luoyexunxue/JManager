<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="{{package}}.storage.{{name}}DAL">
	<insert id="insert" parameterType="{{package}}.model.{{name}}">
		INSERT INTO {{prefix}}_{{nameL}} (
		id,
		{{#repeat columns separator=","}}
		{{columns.name}}
		{{#repeat}}
		) VALUES (
		#{id},
		{{#repeat columns separator=","}}
		#{{{columns.name}}}
		{{#repeat}}
		)
	</insert>
	<update id="update" parameterType="{{package}}.model.{{name}}">
		UPDATE {{prefix}}_{{nameL}} SET
		{{#repeat columns separator=","}}
		{{columns.name}}=#{{{columns.name}}}
		{{#repeat}}
		WHERE id = #{id}
	</update>
	<delete id="delete" parameterType="java.lang.String">
		DELETE FROM {{prefix}}_{{nameL}} WHERE id IN
		<foreach item="id" index="index" collection="array" open="(" separator="," close=")">#{id}</foreach>
	</delete>
	<select id="item" parameterType="java.lang.String" resultType="{{package}}.model.{{name}}">
		SELECT T.* FROM {{prefix}}_{{nameL}} T WHERE T.id=#{value}
	</select>
	<select id="list" parameterType="java.lang.String" resultType="{{package}}.model.{{name}}">
		SELECT T.* FROM {{prefix}}_{{nameL}} T
		<if test="filter != null and filter != ''">WHERE ${value}</if>
	</select>
	<select id="page" parameterType="java.util.Map" resultType="{{package}}.model.{{name}}">
		SELECT T.* FROM {{prefix}}_{{nameL}} T
		<if test="search != null and search != ''">WHERE T.{{search}} LIKE CONCAT('%', #{search}, '%')</if>
		<if test="sort != null and sort != ''">ORDER BY ${sort} ${order}</if>
		LIMIT #{offset},#{limit};
	</select>
	<select id="page_count" parameterType="java.util.Map" resultType="java.lang.Integer">
		SELECT COUNT(1) FROM {{prefix}}_{{nameL}} T
		<if test="search != null and search != ''">WHERE T.{{search}} LIKE CONCAT('%', #{search}, '%')</if>
	</select>
</mapper>