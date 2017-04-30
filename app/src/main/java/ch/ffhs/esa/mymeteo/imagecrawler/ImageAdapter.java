package ch.ffhs.esa.mymeteo.imagecrawler;

import android.support.v4.util.SimpleArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import ch.ffhs.esa.mymeteo.ImageCrawlerActivity;

public class ImageAdapter extends BaseAdapter {

    private ImageCrawlerActivity context;
    private SimpleArrayMap<Integer, String> results;

    public ImageAdapter(ImageCrawlerActivity context) {
        this.results = new SimpleArrayMap<Integer, String>();
        this.context = context;
    }

    public int getCount() {
        return results.size();
    }

    public Object getItem(int position) {
        return results.valueAt(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public void addResult(ImageResult result){
        this.results.put(result.resultIndex, result.imgUrl);
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, 300));
        } else {
            imageView = (ImageView) convertView;
        }

        String imageUrl = results.get(results.keyAt(position));
        imageView.setImageBitmap(context.imageCache.get(imageUrl));

        return imageView;
    }
}

