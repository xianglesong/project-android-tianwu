package xianglesong.com.twandroid.domain.search;

import android.graphics.drawable.Drawable;

import xianglesong.com.twandroid.domain.BaseDto;

public class SearchHistoryDto extends BaseDto {
    private int id;
    private String keyword;
    private Drawable image;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /*public Drawable getImage() {
        return image;
    }*/

   /* public void setImage(Drawable image) {
        this.image = image;
    }*/
}
