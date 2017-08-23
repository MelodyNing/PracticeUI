package services;

import access.DBAccess;
import com.google.gson.Gson;
import entity.AreaInfo;
import entity.ServiceResult;
import entity.UserInfo;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2017/7/12.
 */
public class PracticeServices {
    private static PracticeServices instance = null;
    private static QueryRunner queryRunner = new QueryRunner(DBAccess.getDataSource());
    private static Gson gson = new Gson();

    public static PracticeServices getInstance() {
        if (instance == null) {
            instance = new PracticeServices();
        }
        return instance;
    }

    public String getLoginResult(String jsonArgs, String arguments) {
        ServiceResult serviceResult = new ServiceResult();

        try {
            if (arguments.equals("")) {
                serviceResult.setCode(1);
                serviceResult.setMesssage("getLoginResult 参数为空！");
            }
            UserInfo user = gson.fromJson(arguments,UserInfo.class);
            String sql = "SELECT t1.*,areaname FROM userinfo t1  LEFT JOIN areainfo t2 ON t1.`areacode`=t2.`areacode` WHERE t1.`username`='" + user.getUsername() + "' AND t1.`password`='" + user.getPassword() + "'";
            UserInfo userInfo = queryRunner.query(sql,new BeanHandler<UserInfo>(UserInfo.class));
            serviceResult.setData(userInfo);
        } catch (Exception e) {
            System.out.println("++++++++++++++++++++++getLoginResult catch an exception:" + e.getMessage());
            //e.printStackTrace();
            serviceResult.setCode(-1);
            serviceResult.setMesssage("getLoginResult catch an exception:" + e.getMessage());

        }
        String val = gson.toJson(serviceResult);
        System.out.println("++++++++++++++++++++++getLoginResult return :"+val);
        return val;
    }

    public static void main(String[] args) {
        try {
            UserInfo userInfo=new UserInfo();
            userInfo.setUsername("admin");
            userInfo.setPassword("admin");
            String u = gson.toJson(userInfo);
            System.out.println(u);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String updateUser(String jsonArgs, String arguments) {
        //返回0 正常 1msg  3异常

        ServiceResult serviceResult = new ServiceResult();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            UserInfo user = gson.fromJson(jsonArgs,UserInfo.class);
            int flag=user.getFlag();
            String username=user.getUsername();
            String sql = "";

            if(flag==3){//删除
                sql="DELETE FROM userinfo WHERE username = '"+username+"'";
            }else{
                String password=user.getPassword();
                String areacode=user.getAreacode();
                String remarks=user.getRemarks();
                String createtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                if(flag==1){//添加
                    sql="INSERT INTO `practice`.`userinfo`(`username`,`password`,`areacode`,`remarks`,`createtime`)\n" +
                            "VALUES ('"+username+"','"+password+"','"+areacode+"','"+remarks+"','"+createtime+"')";
                }else if(flag==2){//修改
                    sql="UPDATE `practice`.`userinfo` SET  " +
                            "`password` = '"+password+"',\n" +
                            "  `areacode` = '"+areacode+"',\n" +
                            "  `remarks` = '"+remarks+"',\n" +
                            "  `createtime` = '"+createtime+"'\n" +
                            "WHERE `username` = '"+username+"'";
                }
            }
            conn = DBAccess.getConnection();
            stat = conn.createStatement();
            stat.execute(sql);
        } catch (Exception e) {
            System.out.println("++++++++++++++++++++++updateUser catch an exception:" + e.getMessage());
            serviceResult.setCode(-1);
            serviceResult.setMesssage("updateUser catch an exception:" + e.getMessage());

        }
        System.out.println("++++++++++++++++++++++updateUser return :"+serviceResult.toString());
        return serviceResult.toString();
    }

    public String getAllCities(String jsonArgs, String arguments) {
        ServiceResult serviceResult = new ServiceResult();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            String sql = "select * from areainfo where pareacode='32'";
            List<AreaInfo> citylist=queryRunner.query(sql,new BeanListHandler<AreaInfo>(AreaInfo.class));
            serviceResult.setData(citylist);
            serviceResult.setCode(0);
        } catch (Exception e) {
            System.out.println("++++++++++++++++++++++getAllCities catch an exception:" + e.getMessage());
            serviceResult.setCode(-1);
            serviceResult.setMesssage("getAllCities catch an exception:" + e.getMessage());
        }
        System.out.println("++++++++++++++++++++++getAllCities return :"+serviceResult.toString());
        return serviceResult.toString();
    }

    public String getRegions(String jsonArgs, String argument) {
        ServiceResult serviceResult = new ServiceResult();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            AreaInfo area = gson.fromJson(jsonArgs,AreaInfo.class);
            String pcode = area.getAreacode();
            String sql = "SELECT * FROM areainfo WHERE pareacode='"+pcode+"'";
            List<AreaInfo> areaLst=queryRunner.query(sql,new BeanListHandler<AreaInfo>(AreaInfo.class));
            serviceResult.setCode(0);
            serviceResult.setData(areaLst);
        } catch (Exception e) {
            System.out.println("++++++++++++++++++++++getRegions catch an exception:" + e.getMessage());
            serviceResult.setCode(-1);
            serviceResult.setMesssage("getRegions catch an exception:" + e.getMessage());

        }
        System.out.println("++++++++++++++++++++++getRegions return :"+serviceResult.toString());
        return gson.toJson(serviceResult);
    }

    public String getAllUsers(String jsonArgs, String arguments) {
        ServiceResult serviceResult = new ServiceResult();
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT t1.*,areaname FROM userinfo t1  LEFT JOIN areainfo t2 ON t1.`areacode`=t2.`areacode` WHERE 1=1 AND " ;
            UserInfo user = gson.fromJson(jsonArgs,UserInfo.class);

            String areacode=user.getAreacode();
            sql+="t1.`areacode` LIKE '"+areacode+"%' AND ";
            String username=user.getUsername();
            sql+=" username LIKE '%"+username+"%' AND ";
            String cdate=user.getCreatetime();
            sql+="createtime like '"+cdate+"%'";

            sql+=" ORDER BY createtime DESC";

            List<UserInfo> userLst=queryRunner.query(sql,new BeanListHandler<UserInfo>(UserInfo.class));
            serviceResult.setData(userLst);
            serviceResult.setCode(0);

        } catch (Exception e) {
            System.out.println("++++++++++++++++++++++getAllUsers catch an exception:" + e.getMessage());
            serviceResult.setCode(-1);
            serviceResult.setMesssage("getAllUsers catch an exception:" + e.getMessage());

        }
        System.out.println("++++++++++++++++++++++getAllUsers return :"+serviceResult.toString());
        return serviceResult.toString();
    }

    public String getAllAreas(String jsonArgs, String arguments) {
        return "";
    }
}
