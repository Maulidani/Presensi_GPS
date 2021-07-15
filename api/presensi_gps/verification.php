<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='POST') {
    // header("Content-Type: application/json");

    $response = array();
    $id = $_POST['id'];
    $status = $_POST['status'];
    $type = $_POST['type'];

    if ($type==="presence") {

        $sql = "SELECT * FROM tb_presences WHERE id = '$id' AND status = '$status'";
        $check = mysqli_fetch_array(mysqli_query($conn,$sql));
     
        if(isset($check)){
        
            $response["value"] = 0;
            $response["message"] = "Anda sudah melakukan sebelumnya";
            echo json_encode($response);
        } else {
        
            $sql = "UPDATE tb_presences SET status = '$status' WHERE id = '$id'";
          
            if(mysqli_query($conn,$sql)) {
                 
            $response["value"] = 1;
            $response["message"] = "Sukses";
            echo json_encode($response);
            } else {
    
            $response["value"] = 0;
            $response["message"] = "Gagal";
            echo json_encode($response);
            }
        }

    }else if ($type==="report"){

        $sql = "SELECT * FROM tb_sales_reports WHERE id = '$id' AND status = '$status'";
        $check = mysqli_fetch_array(mysqli_query($conn,$sql));
     
        if(isset($check)){
        
            $response["value"] = 0;
            $response["message"] = "Anda sudah melakukan sebelumnya";
            echo json_encode($response);
        } else {
        
            $sql = "UPDATE tb_sales_reports SET status = '$status' WHERE id = '$id'";
          if(mysqli_query($conn,$sql)) {
                 
            $response["value"] = 1;
            $response["message"] = "Sukses";
            echo json_encode($response);
            } else {
    
            $response["value"] = 0;
            $response["message"] = "Gagal";
            echo json_encode($response);
            }
        }
    }
    mysqli_close($conn);

} else {

    $response["value"] = 0;
    $response["message"] = "Terjadi Kesalahan";
    echo json_encode($response);
}
?>