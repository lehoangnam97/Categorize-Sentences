package co.lehoangnam.nearbydemo;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    /*
    Hãy tưởng tượng GoogleAPIClient như một giao thức ảo giữa 2 máy với nhau, tất cả mọi thứ giao tiếp đều phải thông qua nó

    Các hàm dùng tới GoogleAPIClient được chứa trong connections.Nearby.Connections:



    */
    private GoogleApiClient _googleApiClient;



    private String SERVICE_ID = "NearbyNhom36";
    public static final String ClientName = "LeHoangNam";
    private String _otherEndPointId="Nothing";
    private String _msgToSend;
    private Payload _msgPayload;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        _googleApiClient.connect();
    }

    @Override
    public void onStop() {
        //gọi hàm onStop của lớp cha
        super.onStop();
        //Ngắt kết nối nếu api google có kết nối
        if (_googleApiClient != null && _googleApiClient.isConnected()) {
            _googleApiClient.disconnect();
        }
    }

    /*
   Ham Callback duoc goi trong cac truong hop sau:
       -Khi thiet bi khac co gang ket noi
       -khi thiet bi khac hoan thanh ket noi
       -khi thiet bi khac ngat ket noi
    */

    //
    private final PayloadCallback _payloadCallback=new PayloadCallback() {
        @Override
        public void onPayloadReceived(String s, Payload payload) {
             String strExtrasPayload=payload.toString();
        }

        @Override
        public void onPayloadTransferUpdate(String s, PayloadTransferUpdate payloadTransferUpdate) {
            String result="kết quả vận chuyển tin nhắn : ";
            switch (payloadTransferUpdate.getStatus())
            {
                case (PayloadTransferUpdate.Status.IN_PROGRESS):
                    result+="Đang vận chuyển ";
                case (PayloadTransferUpdate.Status.SUCCESS):
                    result+="Thành công ! ";break;
                case (PayloadTransferUpdate.Status.FAILURE):
                    result+="Thất bại ! ";break;
            }
        }
    };


    //Vòng lặp kết nối
    private final ConnectionLifecycleCallback _connectionLifecyleCallback = new ConnectionLifecycleCallback() {

        @Override
        public void onConnectionInitiated(final String endpointId, ConnectionInfo connectionInfo) {
            Log.i("Kiemtra", "Ket noi " + endpointId + " duoc khoi tao");
            Toast.makeText(MainActivity.this, "Ket noi duoc khoi tao", Toast.LENGTH_SHORT).show();
            //establishConnection(endpointId);
            //Tự động khởi tạo kết nối
            //Nearby.Connections.acceptConnection(_googleApiClient, endpointId, _payloadCallback);
            //Tạo một alert Dialog để thông báo là đã khởi tạo kết nối và chờ đồng ý kết nối
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Chấp nhận kết nối tới " + connectionInfo.getEndpointName())
                    .setMessage("Xin xác nhận nếu mật mã :  " + connectionInfo.getAuthenticationToken() + " cũng được hiển thị trên thiết bị kia")
                    .setPositiveButton("Tôi chấp nhận", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Nếu ấn cái nút này thì đồng ý
                            Nearby.Connections.acceptConnection(_googleApiClient, endpointId, _payloadCallback);
                        }
                    } )
                    .setNegativeButton("Tôi từ chối",new DialogInterface.OnClickListener(){
                       public void onClick(DialogInterface dialog, int which)
                       {
                           Nearby.Connections.rejectConnection(_googleApiClient,endpointId);

                       }
                    }).show();
            ;
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            //markStudentAsPresent(endpointId);
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    //Đã kết nối được, bắt đầu có thể gửi nhận
                    Toast.makeText(MainActivity.this,"Bây giờ có thể truyền tin cho nhau rồi đấy",Toast.LENGTH_SHORT).show();
                    _otherEndPointId=endpointId;
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // Khi kết nối bị từ chối từ một phía
                    Toast.makeText(MainActivity.this,"Có 1 bên đã ngắt kết nối ",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            Log.i("Kiemtra", endpointId + " disconnected");
            Toast.makeText(MainActivity.this, "Da ngat ket noi", Toast.LENGTH_SHORT).show();
            _otherEndPointId="Nothing";
        }
    };

    private void startAdvertising() {
        Nearby.Connections.startAdvertising(_googleApiClient,
                ClientName,//Tên người quảng cáo
                SERVICE_ID,//tên Service do mình quy định
                _connectionLifecyleCallback,//Vòng lặp được đặt
                new AdvertisingOptions(Strategy.P2P_CLUSTER))// hình thức kết nối
                .setResultCallback(
                        new ResultCallback<Connections.StartAdvertisingResult>() {
                            @Override
                            public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // We're advertising!
                                    Log.i("Kiemtra", "Da quang cao thanh cong");
                                    Toast.makeText(MainActivity.this, "Quang cao thanh cong", Toast.LENGTH_SHORT).show();
                                } else {
                                    // We were unable to start advertising.
                                    Log.i("Kiemtra", "Quang cao that bai");
                                    Toast.makeText(MainActivity.this, "Quang cao that bai", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }

    private void startDiscoverting() {
        Nearby.Connections.startDiscovery(
                _googleApiClient,
                SERVICE_ID,
                _EndpointDiscoveryCallback,
                new DiscoveryOptions(Strategy.P2P_CLUSTER))
                .setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Toast.makeText(MainActivity.this, "Tim kiem thiet bi thanh cong", Toast.LENGTH_SHORT).show();
                                    Log.i("Kiemtra", "Tim kiem thiet bi thanh cong");
                                } else {
                                    Toast.makeText(MainActivity.this, "Tim kiem thiet bi that bai", Toast.LENGTH_SHORT).show();
                                    Log.i("Kiemtra", "Tim kiem thiet bi that bai");

                                }
                            }
                        }
                );

    }

    private final EndpointDiscoveryCallback _EndpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(final String endpointId, DiscoveredEndpointInfo dei) {
                    Nearby.Connections.requestConnection(
                            _googleApiClient,
                            ClientName,
                            endpointId,
                            _connectionLifecyleCallback)
                            .setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            if (status.isSuccess()) {
                                                // We successfully requested a connection. Now both sides
                                                // must accept before the connection is established.
                                                Toast.makeText(MainActivity.this,"Gửi yêu cầu tới "+endpointId+" thành công",Toast.LENGTH_SHORT).show();

                                            } else {
                                                // Nearby Connections failed to request the connection.
                                                Toast.makeText(MainActivity.this,"Gửi yêu cầu tới "+endpointId+" thất bại",Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away,
                    // perhaps we might want to do some cleanup here
                    Log.i("Kiemtra", endpointId + " vừa mới tắt quảng cáo");
                }
            };

    public void requestConnection(String endpointId) {

    }

    private void sendMessage()
    {
        String strMsg="Tin nhắn tới";
        _msgPayload=Payload.fromBytes(_msgToSend.getBytes());
        Nearby.Connections.sendPayload(_googleApiClient,_otherEndPointId,_msgPayload);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        _googleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
