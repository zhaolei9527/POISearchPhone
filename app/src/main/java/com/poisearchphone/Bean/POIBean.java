package com.poisearchphone.Bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * com.poisearchphone.Bean
 *
 * @author 赵磊
 * @date 2018/10/8
 * 功能描述：
 */
public class POIBean {

    /**
     * status : 1
     * count : 7
     * pois : [{"name":"巴奴毛肚火锅(商城路店)","address":"东明路与商城路交叉口向东10米路北","tel":"4000232577","pname":"河南省","cityname":"郑州市","adname":"管城回族区"}]
     */

    private String status;
    private String count;
    private List<PoisBean> pois;

    public static List<POIBean> arrayPOIBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<POIBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<PoisBean> getPois() {
        return pois;
    }

    public void setPois(List<PoisBean> pois) {
        this.pois = pois;
    }

    public static class PoisBean {
        /**
         * name : 巴奴毛肚火锅(商城路店)
         * address : 东明路与商城路交叉口向东10米路北
         * tel : 4000232577
         * pname : 河南省
         * cityname : 郑州市
         * adname : 管城回族区
         */

        private String name;
        private String address;
        private String tel;
        private String pname;
        private String cityname;
        private String adname;

        public static List<PoisBean> arrayPoisBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<PoisBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }

        public String getCityname() {
            return cityname;
        }

        public void setCityname(String cityname) {
            this.cityname = cityname;
        }

        public String getAdname() {
            return adname;
        }

        public void setAdname(String adname) {
            this.adname = adname;
        }
    }
}
