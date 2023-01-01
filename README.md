
# Safe Road

This Project is about ensuring safety on road. The project is devided into two part. 
   
    1. Ensuing Assistance of the Driver
    2. Fully Monitoring of the vechile.

### Ensuing Driver Assistance: 
    We would like to detect the traffic sign by a trained Machine Learing model and alert the driver before 
    reaching near the traffic sign.
    Here YOLOv4 model is used to detect this. 
    # The ipynb file contains the code for this.
    Currently this model can detect only 4 sign. 
        
        1. Prohibitory
        2. Mandetory
        3. Danger
        4. Other

### Monitoring of vechile:
    This is done with an android app which would be used to track the buses
    current location.
    The application provides some more exiting feature for the admin pannel like:
        
        1. Designing a route from google maps.
        2. Assigning some point in the maps with a specific time schedule which the vechile must follow. 
        3. Editing or Deleting the route.
        4. Assigning a specific route to a trip.
        5. Seing the overall report of this trip (Late on the which point, Arrival Time etc).
        
# Flow Chart of The System
   ### Traffic Sign Detection:
   ![s4](https://user-images.githubusercontent.com/85815740/210074181-d962e58d-5dfb-46cb-9f63-79e6ce8bd770.jpg)

   ### Admin Side of The Application:  
   ![Slide2](https://user-images.githubusercontent.com/85815740/210074206-a0f1748c-8f6b-4b3e-81fc-42e8ae618d64.JPG)
   ### Bus Driver Side of The Application :
   ![Slide1](https://user-images.githubusercontent.com/85815740/210074194-66b8f572-6647-4d58-a545-ff6b5b7627a9.jpg)

# Schema Diagram of the Application:
   ![Slide3](https://user-images.githubusercontent.com/85815740/210074219-ed92ff40-c152-4e9e-b027-9f686415c441.JPG)


# Screenshot of Application:
   ### Traffic Sign Detection:
   ![download](https://user-images.githubusercontent.com/85815740/210075189-e6651464-aec9-4775-826e-6c39f96fd69d.png)

   
   ## Screenshot of Android Application:
   
   <table>
   <tr>
    <td colspan = 2 align="center"><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Common/Screenshot_20221227-012103.jpg" height="400" width="200"> </td>
    <td colspan = 2 align="center">
      <img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Common/Screenshot_20221227-012107.jpg" height="400" width="200"> 
      </td>
   </tr>
   <tr> 
      <td colspan = 2 align = "center" >Splash Screen</td>
      <td colspan = 2 align = "center" > Choosing Account Type</td>
   </tr>
   <tr>
      <td colspan = 4 align="center"> <b> BUS DRIVER SIDE</b> </td>
   </tr>
   <tr>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Bus%20Driver/Screenshot_20221227-012647.jpg" height="400" width="200"> </td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Bus%20Driver/Screenshot_20221227-012651.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Bus%20Driver/Screenshot_20221227-012658.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Bus%20Driver/Screenshot_20221227-012703.jpg" height="400" width="200"></td>
    
   </tr>
   <tr> 
      <td align = "center" >Choose The Activity</td>
      <td align = "center" > Page for Detecting Traffic Sign</td>
      <td align = "center" >Page for Bus Drive Action</td>
      <td align = "center" > Stating Journey</td>
   </tr>
   <tr>
    <td align="center"><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Bus%20Driver/Screenshot_20221227-012705.jpg" height="400" width="200"> </td>
    <td align="center">
      <img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Bus%20Driver/Screenshot_20221227-012750.jpg" height="400" width="200"> 
      </td>
   </tr>
   <tr> 
      <td align = "center" >Navigation Drawer </td>
      <td align = "center" > Route Traveled By the bus</td>
   </tr>
   <tr>
      <td colspan = 4 align="center"> <b>ADMIN SIDE</b> </td>
   </tr>
   <tr>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012111.jpg" height="400" width="200"> </td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/66fe152a0b5558e561b78a35dc4bd49f27575b9d/image/Admin/Screenshot_20221227-012122.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/66fe152a0b5558e561b78a35dc4bd49f27575b9d/image/Admin/Screenshot_20221227-012128.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012144.jpg" height="400" width="200"></td>
   </tr>
    
   <tr> 
      <td align = "center" >Admin Password Dialog</td>
      <td align = "center" > Admin Pannel </td>
      <td align = "center" >Choosing Select Route</td>
      <td align = "center" >Entering Source Destination and ID</td>
   </tr>
   
   <tr>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012221.jpg" height="400" width="200"> </td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012244.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012317.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012346.jpg" height="400" width="200"></td>
   </tr>
   <tr> 
      <td align = "center" >Setting The Flags</td>
      <td align = "center" > Reached The Destination</td>
      <td align = "center" >Add New Journey</td>
      <td align = "center" >Entered Infromation for Trip</td>
   </tr>
   
   <tr>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012414.jpg" height="400" width="200"> </td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012435.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-012627.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20230101-235120.png" height="400" width="200"></td>
   </tr>
   <tr> 
      <td align = "center" >Selecting The bus ID</td>
      <td align = "center" > Tracking The bus </td>
      <td align = "center" >Getting Bus Id for Report </td>
      <td align = "center" >Report of The trip</td>
   </tr>
   
   <tr>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-234030.jpg" height="400" width="200"> </td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-234750(1).jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221227-234829.jpg" height="400" width="200"></td>
    <td><img src="https://github.com/abrarhasan3/System_For_Safe_Road/blob/main/image/Admin/Screenshot_20221228-000322.jpg" height="400" width="200"></td>
   </tr>
   <tr> 
      <td align = "center" >Editing The Route</td>
      <td align = "center" > Edit Options </td>
      <td align = "center" >Searching For Specific Location</td>
      <td align = "center" >Setting The Timer</td>
   </tr>
</table>
   
   



## Authors

- [Abrar Hasan](https://github.com/abrarhasan3)
- [Nishat Sadaf Lira](https://github.com/Lira1999)



