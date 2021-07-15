<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='GET') {
  $sql = "SELECT * FROM tb_offices ";
  $res = mysqli_query($conn,$sql);
  $result = array();

  $check = mysqli_fetch_array(mysqli_query($conn,$sql));
    if(isset($check)){
        $response["value"] = 1;
        $response["message"] = "Sukses";
        $response["name"] = $check[1];
        $response["latitude"] = $check[2];
        $response["longitude"] = $check[3];
        $response["radius"] = $check[4];
        echo json_encode($response);
    } else {
      $response["value"] = 0;
      $response["message"] = "Terjadi kesalahan";
      echo json_encode($response);
    }
  mysqli_close($conn);
} else {
  $response["value"] = 0;
  $response["message"] = "Terjadi kesalahan";
  echo json_encode($response);
}
?>