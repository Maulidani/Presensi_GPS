<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='GET') {

    $idName = $_GET['id'];
    $position = $_GET['position'];

    if($idName !== ""){

      $sql = "SELECT tb_users.id, tb_users.name, tb_positions.position, tb_users.email, tb_users.password, tb_users.img, tb_users.status 
        FROM tb_users, tb_positions 
        WHERE tb_users.position = tb_positions.id 
        AND tb_users.id = '$idName'
        ORDER BY tb_users.id DESC";
        $res = mysqli_query($conn,$sql);
        $result = array();
        
        $check = mysqli_fetch_array($res);
        if(isset($check)){
            $response["value"] = 1;
            $response["message"] = "Sukses";
            $response["status"] = $check[6];
            echo json_encode($response);
        } else {
            $response["value"] = 0;
            $response["message"] = "Gagal";
            echo json_encode($response);
        } 
        mysqli_close($conn);

      } else if ($position !== ""){

          $sql = "SELECT tb_users.id, tb_users.name, tb_positions.position, tb_users.email, tb_users.password, tb_users.img, tb_users.status, tb_users.img 
          FROM tb_users, tb_positions 
          WHERE tb_users.position = tb_positions.id 
          AND  tb_positions.position = '$position'
          ORDER BY tb_users.id DESC";
          $res = mysqli_query($conn,$sql);
          $result = array();
    
          $linkImg = "http://192.168.43.223/presensi_gps/";
          
          while($row = mysqli_fetch_array($res)){
            array_push($result, array(
              'id'=>$row[0],
              'name'=>$row[1],
              'position'=>$row[2],
              'email'=>$row[3],
              'password'=>$row[4],
              'img'=>$row[5],
              'status'=>$row[6],
              'img'=>$linkImg.$row[7]
              ));
          }
          echo json_encode(array("value"=>1,"result"=>$result));
          mysqli_close($conn);
     
    } else {
      
      $sql = "SELECT tb_users.id, tb_users.name, tb_positions.position, tb_users.email, tb_users.password, tb_users.img, tb_users.status, tb_users.img 
      FROM tb_users, tb_positions 
      WHERE tb_users.position = tb_positions.id 
      ORDER BY tb_users.id DESC";
      $res = mysqli_query($conn,$sql);
      $result = array();

      $linkImg = "http://192.168.43.223/presensi_gps/";
      
      while($row = mysqli_fetch_array($res)){
        array_push($result, array(
          'id'=>$row[0],
          'name'=>$row[1],
          'position'=>$row[2],
          'email'=>$row[3],
          'password'=>$row[4],
          'img'=>$row[5],
          'status'=>$row[6],
          'img'=>$linkImg.$row[7]
          ));
      }
      echo json_encode(array("value"=>1,"result"=>$result));
      mysqli_close($conn);
    }
}
?>