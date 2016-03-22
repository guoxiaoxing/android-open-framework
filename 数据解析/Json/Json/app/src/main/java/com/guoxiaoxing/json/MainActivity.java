package com.guoxiaoxing.json;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    TextView mTvJson;
    ImageView mIvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvJson = (TextView) findViewById(R.id.tv_json);
        mIvImage = (ImageView) findViewById(R.id.iv_image);


        Gson gson = new Gson();

//        mTvJson.setText(getJsonFromAssets("meitu.json"));
        Toast.makeText(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/meitu.json", Toast.LENGTH_SHORT).show();

        if(isExternalStorageReadable()){

            mTvJson.setText(getJsonFromSD(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"meitu.json"));
            mIvImage.setImageBitmap(decodeBitmapFromSD(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "bottom1.png"));
        }

    }


    /**
     * 读取本地文件中JSON字符串
     *
     * @param fileName
     * @return
     */
    private String getJsonFromAssets(String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    getAssets().open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 读取本地文件中JSON字符串
     *
     * @param filePath
     * @return
     */
    private String getJsonFromSD(String filePath) {

        File file = new File(filePath);
        String json = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel fileChannel = fileInputStream.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            json = Charset.defaultCharset().decode(mappedByteBuffer).toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * 检查SD卡是否可读取／写入
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 检查SD是否可读取
     * @return
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public Bitmap decodeBitmapFromSD(String filePath){

        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return  bitmap;

    }


}
