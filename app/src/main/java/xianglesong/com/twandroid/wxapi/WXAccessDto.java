package xianglesong.com.twandroid.wxapi;

import java.io.Serializable;

public class WXAccessDto implements Serializable {

    /**
     * 用户的标识，对当前开发者帐号唯一
     */
    public String openid;

    /**
     * 用户昵称
     */
    public String nickname;

    /**
     * 用户性别  1为男性，2为女性
     */
    public String sex;

    /**
     * 用户个人资料填写的省份
     */
    public String province;

    /**
     * 用户个人资料填写的城市
     */
    public String city;

    /**
     * 国家，如中国为CN
     */
    public String country;

    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
     */
    public String headimgurl;

    /**
     * 用户特权信息，json数组，用逗号隔开
     */
    public String privilege;

    /**
     * 用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
     */
    public String unionid;


    public WXAccessDto() {

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
