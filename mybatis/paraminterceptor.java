package cn.com.lawson.base.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

@Intercepts({ @Signature(method = "update", args = { MappedStatement.class, Object.class }, type = Executor.class),
		@Signature(method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,
				ResultHandler.class }, type = Executor.class) })
public class SchemaInterceptor implements Interceptor {

	protected static Log LOG = LogFactory.getLog(SchemaInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		Object[] args = invocation.getArgs();

		MappedStatement mappedStatement = (MappedStatement) args[0];

		SqlSource sqlSource = mappedStatement.getSqlSource();

		// 只拦截动态sql
		if (sqlSource instanceof DynamicSqlSource) {

			// 获取到sqlNode对象
			Field field = DynamicSqlSource.class.getDeclaredField("rootSqlNode");
			field.setAccessible(true);
			SqlNode sqlnode = (SqlNode) field.get(sqlSource);

			// 获取动态代理对象
			SqlNode proxyNode = proxyNode(sqlnode);

			field.set(sqlSource, proxyNode);
		}

		return invocation.proceed();

	}

	private SqlNode proxyNode(SqlNode sqlnode) {
		SqlNode proxyNode = (SqlNode) Proxy.newProxyInstance(sqlnode.getClass().getClassLoader(),
				new Class[] { SqlNode.class }, new SqlNodeInvocationHandler(sqlnode));
		return proxyNode;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		LOG.debug("setProperties====>" + properties);
	}

	private class SqlNodeInvocationHandler implements InvocationHandler {

		private SqlNode target;

		public SqlNodeInvocationHandler(SqlNode target) {
			super();
			this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			DynamicContext context = (DynamicContext) args[0];
			context.getBindings().put("dbSchema", "lawson_ph3.");
			return method.invoke(target, args);
		}

	}

}



	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="plugins">
			<array>
				<bean class="cn.com.lawson.base.db.SchemaInterceptor"></bean>
			</array>
		</property>
		<property name="mapperLocations"
			value="classpath:cn/com/lawson/web/admin/**/*.sql.xml" />
		<property name="configLocation" value="classpath:config/mybatis-configuration.xml" />
	</bean>
