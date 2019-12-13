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
	<select id="select" parameterType="java.lang.String" resultType="{{package}}.model.{{name}}">
		SELECT T.* FROM {{prefix}}_{{nameL}} T WHERE T.id=#{value}
	</select>
	<sql id="condition">
		<trim prefix="WHERE" prefixOverrides="AND | OR">
			<if test="{{search}} != null and {{search}} != ''">AND T.{{search}} LIKE CONCAT('%', #{{{search}}}, '%')</if>
		</trim>
	</sql>
	<select id="list" parameterType="{{package}}.model.{{name}}" resultType="{{package}}.model.{{name}}">
		SELECT T.* FROM {{prefix}}_{{nameL}} T
		<include refid="condition" />
	</select>
	<select id="page" parameterType="java.util.Map" resultType="{{package}}.model.{{name}}">
		SELECT T.* FROM {{prefix}}_{{nameL}} T
		<include refid="condition" />
		<if test="sort != null and sort != ''">ORDER BY ${sort} ${order}</if>
		LIMIT #{offset},#{limit};
	</select>
	<select id="page_count" parameterType="java.util.Map" resultType="java.lang.Integer">
		SELECT COUNT(1) FROM {{prefix}}_{{nameL}} T
		<include refid="condition" />
	</select>
</mapper>