package animalize.github.com.quantangshi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import animalize.github.com.quantangshi.Database.BackupUtil;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.UIPoem.URI2Path;

public class BackupActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int FILE_SELECT_CODE = 22;
    // 备份
    private TextView locPath;
    private String path;

    public static void actionStart(Context context) {
        Intent i = new Intent(context, BackupActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        locPath = (TextView) findViewById(R.id.loc_path);

        path = "/" + BackupUtil.getDirName();
        locPath.setText(path);

        Button bt = (Button) findViewById(R.id.button_vacuum);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.button_backup);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.button_choose);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_vacuum:
                int s1, s2;
                s1 = MyDatabaseHelper.getDBSize();
                MyDatabaseHelper.vacuum();
                s2 = MyDatabaseHelper.getDBSize();

                Toast.makeText(this,
                        "紧凑前文件大小：" + s1 + "\n紧凑后文件大小：" + s2,
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_backup:
                if (!allowPermission()) {
                    requestPermission(1);
                } else {
                    doBackup();
                }
                break;

            case R.id.button_choose:
                if (!allowPermission()) {
                    requestPermission(2);
                } else {
                    chooseFile();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == FILE_SELECT_CODE) {
            Uri uri = data.getData();
            final String path = URI2Path.getPath(this, uri);
            if (isDBFile(path)) {
                AlertDialog.Builder d = new AlertDialog.Builder(this);
                d.setTitle("确认还原操作");
                d.setMessage("是否用" + path + "文件进行还原操作？");
                d.setCancelable(false);
                d.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BackupUtil.Restore(path);
                        Toast.makeText(BackupActivity.this,
                                "已恢复为：" + path,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                d.setNegativeButton("取消", null);
                d.show();

            } else {
                String s = "选择的文件名模式不对";
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doBackup() {
        File f = BackupUtil.backup();

        String s = "已保存到:" + f.getAbsolutePath() + "\n共"
                + MyDatabaseHelper.getDBSize() + "字节";
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(intent, FILE_SELECT_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "调用文件管理器失败", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDBFile(String path) {
        String p = ".*QTS\\d{6}_\\d{6}\\.db$";

        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(path);
        return matcher.find();
    }

    // 权限相关

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    doBackup();
                } else {
                    Toast.makeText(this,
                            "您没有授予权限，无法进行备份。",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    chooseFile();
                } else {
                    Toast.makeText(this,
                            "您没有授予权限，无法选择文件。",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean allowPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(int code) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                code);
    }
}
