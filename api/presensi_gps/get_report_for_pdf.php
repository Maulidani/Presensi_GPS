<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='GET') {
  
    $when = $_GET['when'];
    $year  = $_GET['year'];

    if($when !== ""){
      if($when === "today"){

        $sql = "SELECT tb_users.name, tb_sales_reports.date, tb_sales_reports.time
        FROM tb_sales_reports, tb_users 
        WHERE tb_sales_reports.name = tb_users.id
        AND date = '$currentDate'
        AND DATE_FORMAT(tb_sales_reports.date, '%y') = '$year' 
        ORDER BY tb_sales_reports.id DESC";
    
        $res = mysqli_query($conn,$sql);
        $result = array();
        
        while($row = mysqli_fetch_array($res)){
        
          array_push($result, array(
           
            'name'=>$row[0],
            'time'=>$row[2]
            ));
        }
        echo json_encode(array("value"=>1,"result"=>$result));
        mysqli_close($conn);
  
      } else {
        $sql = "SELECT tb_users.name, COUNT(tb_sales_reports.name) AS jumlah , DATE_FORMAT(tb_sales_reports.date, '%m') as date 
        FROM tb_sales_reports, tb_users
        WHERE tb_sales_reports.name = tb_users.id
        AND DATE_FORMAT(tb_sales_reports.date, '%m') = '$when'
        AND DATE_FORMAT(tb_sales_reports.date, '%y') = '$year' 
        GROUP BY tb_sales_reports.name
        ORDER BY tb_sales_reports.id DESC";
    
        $res = mysqli_query($conn,$sql);
        $result = array();
        
        while($row = mysqli_fetch_array($res)){
        
          array_push($result, array(
           
            'name'=>$row[0],
            'jumlah'=>$row[1],
            ));
        }
        echo json_encode(array("value"=>1,"result"=>$result));
        mysqli_close($conn);
        
      }
      
    } else {

        $sql = "SELECT tb_users.name, COUNT(tb_sales_reports.name) AS jumlah , DATE_FORMAT(tb_sales_reports.date, '%m') as date 
        FROM tb_sales_reports, tb_users
        WHERE tb_sales_reports.name = tb_users.id
        AND DATE_FORMAT(tb_sales_reports.date, '%y') = '$year' 
        GROUP BY tb_sales_reports.name
        ORDER BY tb_sales_reports.id DESC";
    
        $res = mysqli_query($conn,$sql);
        $result = array();
        
        while($row = mysqli_fetch_array($res)){
        
          array_push($result, array(
           
            'name'=>$row[0],
            'jumlah'=>$row[1]
            ));
        }
        echo json_encode(array("value"=>1,"result"=>$result));
        mysqli_close($conn);
          
    }
}
?>