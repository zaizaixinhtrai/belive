package com.appster.features.searchScreen;

import com.appster.core.adapter.DisplayableItem;
import com.appster.core.adapter.UpdateableItem;

/**
 * Created by thanhbc on 5/17/17.
 */

public class Header implements UpdateableItem {
    public String title;

    public Header(String title) {
        this.title = title;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        return title != null ? title.equals(header.title) : header.title == null;

    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }

    @Override
    public boolean isSameItem(DisplayableItem item) {
        return item instanceof Header && this.equals(item);
    }

    @Override
    public boolean isSameContent(DisplayableItem item) {
        return this.title.equalsIgnoreCase(((Header) item).title);
    }
}
