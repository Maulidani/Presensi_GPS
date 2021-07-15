<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='GET') {
  
    $today = $_GET['today'];

    if($today !== ""){
      if($today === "today"){

        $sql = "SELECT tb_sales_reports.id, tb_users.name, tb_sales_reports.location_name,
        tb_sales_reports.latitude, tb_sales_reports.longitude,tb_sales_reports.img,
        tb_sales_reports.notes, tb_sales_reports.date, tb_sales_reports.time, 
        tb_sales_reports.status 
        FROM tb_sales_reports, tb_users 
        WHERE tb_sales_reports.name = tb_users.id
        AND date = '$currentDate'
        ORDER BY tb_sales_reports.id DESC";
    
        $res = mysqli_query($conn,$sql);
        $result = array();
    
        $linkImg = "http://192.168.43.223/presensi_gps/";
    
        while($row = mysqli_fetch_array($res)){
        
          array_push($result, array(
           
            'id'=>$row[0],
            'name'=>$row[1],
            'location_name'=>$row[2],
            'latitude'=>$row[3],
            'longitude'=>$row[4],
            'img'=>$linkImg.$row[5],
            'notes'=>$row[6],
            'date'=>strftime("%A, %d %B %Y", strtotime($row[7])),
            'time'=>$row[8],
            'status'=>$row[9],
            ));
        }
        echo json_encode(array("value"=>1,"result"=>$result));
        mysqli_close($conn);
  
      } else {
        echo json_encode(array(
          "value"=>0,
          "message"=> "Terjadi Kesalahan"));
      }
      
    } else {

      $sql = "SELECT tb_sales_reports.id, tb_users.name, tb_sales_reports.location_name,
      tb_sales_reports.latitude, tb_sales_reports.longitude,tb_sales_reports.img,
      tb_sales_reports.notes, tb_sales_reports.date, tb_sales_reports.time, 
      tb_sales_reports.status 
      FROM tb_sales_reports, tb_users 
      WHERE tb_sales_reports.name = tb_users.id
      ORDER BY tb_sales_reports.id DESC";
  
      $res = mysqli_query($conn,$sql);
      $result = array();
    
      while($row = mysqli_fetch_array($res)){
      
        array_push($result, array(
         
          'id'=>$row[0],
          'name'=>$row[1],
          'location_name'=>$row[2],
          'latitude'=>$row[3],
          'longitude'=>$row[4],
          'img'=>$linkImg.$row[5],
          'notes'=>$row[6],
          'date'=>strftime("%A, %d %B %Y", strtotime($row[7])),
          'time'=>$row[8],
          'status'=>$row[9],
          ));
      }
      echo json_encode(array("value"=>1,"result"=>$result));
      mysqli_close($conn);

    }
}
?>