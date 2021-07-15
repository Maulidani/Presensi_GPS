<!-- 
<?php
require_once('connection.php');

if($_SERVER['REQUEST_METHOD']=='GET') {
 
      $sql = "SELECT tb_presences.id, tb_offices.name,tb_users.name,tb_presences.img,tb_presences.date,tb_presences.time,tb_presences.status 
      FROM tb_presences , tb_users , tb_offices
      WHERE tb_presences.name = tb_users.id 
      AND tb_presences.office = tb_offices.id
      ORDER BY tb_presences.id DESC";
      $res = mysqli_query($conn,$sql);
      $result = array();
        
      while($row = mysqli_fetch_array($res)){
        array_push($result, array(
          'id'=>$row[0],
          'office'=>$row[1],
          'name'=>$row[2],
          'img'=>$linkImg.$row[3],
          'date'=>strftime("%A, %d %B %Y", strtotime($row[4])),
          'time'=>$row[5],
          'status'=>$row[6]
          ));
      }
      echo json_encode(array("value"=>1,"result"=>$result));
      mysqli_close($conn);

}
?> 
-->