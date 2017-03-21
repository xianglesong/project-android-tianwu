package xianglesong.com.twandroid.domain.search;

import android.graphics.drawable.Drawable;

import xianglesong.com.twandroid.domain.BaseDto;

public class SearchAutoCompleteDto extends BaseDto {
    private String keyword;
    private Drawable image;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String title) {
        this.keyword = title;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

}
