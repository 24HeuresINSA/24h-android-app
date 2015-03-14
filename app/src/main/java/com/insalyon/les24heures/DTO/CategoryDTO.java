package com.insalyon.les24heures.DTO;

/**
 * Created by remi on 14/03/15.
 */
public class CategoryDTO {
    String _id;
    String name;
    String display_name;
    String icon_name;

    public CategoryDTO(String _id, String name, String display_name, String icon_name) {
        this._id = _id;
        this.name = name;
        this.display_name = display_name;
        this.icon_name = icon_name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public void setIcon_name(String icon_name) {
        this.icon_name = icon_name;
    }
}
