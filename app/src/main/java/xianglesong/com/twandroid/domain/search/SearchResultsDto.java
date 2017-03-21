package xianglesong.com.twandroid.domain.search;

import android.graphics.drawable.Drawable;

import xianglesong.com.twandroid.domain.BaseDto;

public class SearchResultsDto extends BaseDto {
    private int id;
    private String title;
    private Drawable image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
