package com.mqtt.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;//存储数据
    private SharedPreferences.Editor editor;//存储数据

    Button buttonSend;
    Button buttonRecClear;
    Button buttonSendClear;
    EditText editTextSend;
    TextView textViewRead;

    //public的类变量
    public static String MqttUserString = "yang";//用户名
    public static String MqttPwdString = "11223344";//密码
    public static String MqttIPString = "47.93.19.134";//IP地址
    public static int MqttPort = 1883;//端口号
    public static String SubscribeString = "/pub";//订阅的主题
    public static String PublishString = "/sub";//发布的主题


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editTextSend = (EditText) findViewById(R.id.editText11);
        editTextSend.setMovementMethod(ScrollingMovementMethod.getInstance());
        textViewRead = (TextView) findViewById(R.id.textView11);
        textViewRead.setMovementMethod(ScrollingMovementMethod.getInstance());

        buttonSend = (Button) findViewById(R.id.button11);
        buttonRecClear = (Button) findViewById(R.id.button12);
        buttonSendClear = (Button) findViewById(R.id.button13);
        buttonSend.setOnClickListener(btnSendClickListener);
        buttonRecClear.setOnClickListener(btnRecClearClickListener);
        buttonSendClear.setOnClickListener(btnSendClearClickListener);


        IntentFilter filter = new IntentFilter();//监听的广播
        filter.addAction("Broadcast.MqttServiceSend");
        registerReceiver(MainActivityReceiver, filter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        MqttUserString = sharedPreferences.getString("MqttUser", "yang");
        MqttPwdString = sharedPreferences.getString("MqttPwd", "11223344");
        MqttIPString = sharedPreferences.getString("MqttIP", "47.93.19.134");
        MqttPort = sharedPreferences.getInt("MqttPort", 1883);
        SubscribeString = sharedPreferences.getString("MqttSub", "/sub");
        PublishString = sharedPreferences.getString("MqttPub", "/pub");

    }

    private View.OnClickListener btnSendClearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editTextSend.setText("");
        }
    };


    private View.OnClickListener btnRecClearClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            textViewRead.setText("");
        }
    };

    private View.OnClickListener btnSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //活动到MQTT广播的发送端(点下按钮)
            String string = editTextSend.getText().toString().replace(" ", "");
            if (string.length() > 0) {
                //活动到MQTT广播的发送端(点下按钮)
                Intent intent = new Intent();
                intent.setAction("ActivitySendMqttService");
                intent.putExtra("OtherActivitySend", "SendData;;" + string);
                sendBroadcast(intent);
            }
        }
    };


    /**配置MQTT对话框*/
    private void MqttConfigAlertDialog(String Title)
    {
        /*自定义布局的对话框实现*/
        AlertDialog.Builder builderMqtt = new AlertDialog.Builder(MainActivity.this);
        View viewMqtt = View.inflate(MainActivity.this, R.layout.dialog_mqtt, null);

        //注意了这里的变量必须是final!!!
        final EditText editTextMqttUser = (EditText) viewMqtt.findViewById(R.id.editText21);//用户名
        final EditText editTextMqttPwd = (EditText) viewMqtt.findViewById(R.id.editText22);//密码
        final EditText editTextMqttIP = (EditText) viewMqtt.findViewById(R.id.editText23);//IP地址
        final EditText editTextMqttPort = (EditText) viewMqtt.findViewById(R.id.editText24);//端口号
        final EditText editTextMqttSub = (EditText) viewMqtt.findViewById(R.id.editText25);//订阅的主题
        final EditText editTextMqttPub = (EditText) viewMqtt.findViewById(R.id.editText26);//发布的主题

        builderMqtt.setTitle(Title);//设置标题
        builderMqtt.setIcon(R.drawable.dialog_icon);	//设置对话框标题前的图标
        builderMqtt.setPositiveButton("确定",null);//实现方法在下面
        builderMqtt.setNegativeButton("默认",null);//实现方法在下面
        builderMqtt.setView(viewMqtt);//设置对话框显示内容

        final AlertDialog alertDialogMqtt = builderMqtt.create();//床架dialog对象
        alertDialogMqtt.setCanceledOnTouchOutside(false);//点击外围不消失
//        alertDialogMqtt.show();//显示

        //初始化(显示对应的监听)
        alertDialogMqtt.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                editTextMqttUser.setText(MqttUserString);
                editTextMqttPwd.setText(MqttPwdString);
                editTextMqttIP.setText(MqttIPString);
                editTextMqttPort.setText(MqttPort+"");
                editTextMqttSub.setText(SubscribeString);
                editTextMqttPub.setText(PublishString);

                editTextMqttUser.setSelection(editTextMqttUser.getText().length());//将光标移至文字末尾
            }
        });
        //show的显示必须在初始化之后！！！
        alertDialogMqtt.show();//显示

        //点击确定后
        alertDialogMqtt.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //获取此时文本框的值
                    String str1 = editTextMqttUser.getText().toString();
                    String str2 = editTextMqttPwd.getText().toString();
                    String str3 = editTextMqttIP.getText().toString();
                    String str4 = editTextMqttPort.getText().toString();
                    String str5 = editTextMqttSub.getText().toString();
                    String str6 = editTextMqttPub.getText().toString();

                    //合法性检验
                    if (str1.length() == 0 || str2.length() == 0 ||str3.length() == 0 ||str4.length() == 0 ||
                            str5.length() == 0 ||str6.length() == 0) {
                        Toast.makeText(getApplicationContext(), "请检查输入",500).show();
                        return;
                    }

                    //赋值操作
                    MqttUserString = str1;//用户名
                    MqttPwdString = str2;//密码
                    MqttIPString = str3;//IP地址
                    MqttPort = Integer.parseInt(str4);//端口号
                    SubscribeString = str5;//订阅的主题
                    PublishString = str6;//发布的主题

                    //保存本次的参数
                    editor = sharedPreferences.edit();
                    editor.putString("MqttUser", MqttUserString);//用户名
                    editor.putString("MqttPwd", MqttPwdString);//密码
                    editor.putString("MqttIP", MqttIPString);//IP地址
                    editor.putInt("MqttPort",MqttPort);//端口号
                    editor.putString("MqttSub",SubscribeString);//订阅的主题
                    editor.putString("MqttPub",PublishString);//发布的主题
                    editor.commit();

                    //发送广播到后台表示设置完成，进行重启复位
                    Intent intent = new Intent();
                    intent.setAction("ActivitySendMqttService");
                    intent.putExtra("OtherActivitySend","ResetMqtt;;");
                    sendBroadcast(intent);


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "存储失败,请检查输入",500).show();
                }


                alertDialogMqtt.dismiss();//取消
            }
        });

        //点击默认后
        alertDialogMqtt.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //赋值操作
                MqttUserString = "yang";//用户名
                MqttPwdString = "11223344";//密码
                MqttIPString = "47.93.19.134";//IP地址
                MqttPort = 1883;//端口号
                SubscribeString = "/pub";//订阅的主题
                PublishString = "/sub";//发布的主题


                editor = sharedPreferences.edit();
                editor.putString("MqttUser", "yang");//用户名
                editor.putString("MqttPwd", "11223344");//密码
                editor.putString("MqttIP", "47.93.19.134");//IP地址
                editor.putInt("MqttPort", 1883);//端口号
                editor.putString("MqttSub", "/sub");//订阅的主题
                editor.putString("MqttPub", "/pub");//发布的主题
                editor.commit();


                //发送广播到后台表示设置完成，进行重启复位
                Intent intent = new Intent();
                intent.setAction("ActivitySendMqttService");
                intent.putExtra("OtherActivitySend", "ResetMqtt;;");
                sendBroadcast(intent);

                alertDialogMqtt.dismiss();//取消
            }
        });
    }

    /**
     * 当活动即将可见时调用
     */
    @Override
    protected void onStart() {
        Intent startIntent = new Intent(getApplicationContext(), ServiceMqtt.class);
        startService(startIntent); //启动后台服务

        IntentFilter filter = new IntentFilter();//监听的广播
        filter.addAction("Broadcast.MqttServiceSend");
        registerReceiver(MainActivityReceiver, filter);

        super.onStart();
    }

    /*该类的广播接收程序*/
    private BroadcastReceiver MainActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String msgString = intent.getStringExtra("MqttServiceSend");//键值对接收
                textViewRead.append(msgString);
                textViewRead.post(new Runnable() {//让滚动条向下移动,永远显示最新的数据
                    @Override
                    public void run() {
                        final int scrollAmount = textViewRead.getLayout().getLineTop(textViewRead.getLineCount()) - textViewRead.getHeight();
                        if (scrollAmount > 0)
                            textViewRead.scrollTo(0, scrollAmount);
                        else
                            textViewRead.scrollTo(0, 0);
                    }
                });
            } catch (Exception e) {

            }
//            Toast.makeText(context, "Receive;;"+msgString, Toast.LENGTH_SHORT).show();
        }
    };


    //首页右上角菜单选项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //新建的xml文件
        setIconsVisible(menu, true);//设置菜单添加图标有效
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //根据不同的id点击不同按钮控制activity需要做的事件
        switch (item.getItemId()) {
            case R.id.action_settings:
                //事件
                MqttConfigAlertDialog("MQTT设置");
                break;
        }
        return true;
    }

    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if (menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
