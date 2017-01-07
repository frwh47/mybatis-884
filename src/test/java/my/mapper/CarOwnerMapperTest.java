package my.mapper;

import my.model.CarOwner;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.*;

import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CarOwnerMapperTest {
    private static SqlSessionFactory sqlSessionFactory;
    private SqlSession session;

    @BeforeClass
    public static void beforeClass() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, "sqlserver");
        reader.close();
    }

    @Before
    public void before() {
        session = sqlSessionFactory.openSession();
    }

    @After
    public void after() {
        session.rollback();
        session.close();
    }

    @Test
    public void run() {
        CarOwnerMapper mapper = session.getMapper(CarOwnerMapper.class);
        CarOwner car = new CarOwner();
        car.setName("Benz");
        Assert.assertNull(car.getId());

        try {
            mapper.insert(car); // exception will be throw out
            Assert.assertNotNull(car.getId());
        } catch (PersistenceException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Test
    public void testGeneratedKeys() throws SQLException {
        String sql = "insert into CarOwner (user_name) values('mike'); ";
        PreparedStatement statement = session.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        //executeUpdate will get generated keys
        statement.executeUpdate();
        //execute will not get generated keys
        //statement.execute();

        ResultSet rs = statement.getGeneratedKeys();
        Assert.assertTrue(rs.next());

        String id = rs.getString(1);
        System.out.println("id = " + id);
        Assert.assertNotNull(id);
        Assert.assertTrue(Integer.parseInt(id) > 0);

        session.getConnection().rollback();
    }
}
