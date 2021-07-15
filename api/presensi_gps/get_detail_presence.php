<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='GET') {
     $idName = $_GET['name'];

      $sql = "SELECT * FROM tb_presences WHERE tb_presences.date = '$currentDate' AND name = '$idName'";
      $res = mysqli_query($conn,$sql);
      $result = array();
        
      $check = mysqli_fetch_array($res);
      if(isset($check)){
          $response["value"] = 1;
          $response["id"] = "Sukses";
          $response["name"] = $check[2];
          $response["img"] = $linkImg.$check[3];
          $response["date"] = strftime("%A, %d %B %Y", strtotime($check[4]));
          $response["time"] = $check[5];
          echo json_encode($response);
      } else {
          $response["value"] = 0;
          $response["message"] = "Gagal";
          echo json_encode($response);
      } 
      mysqli_close($conn);
}
?>