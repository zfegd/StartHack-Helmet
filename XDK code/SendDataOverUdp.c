/**

/* system header files */
#include <stdio.h>

/* additional interface header files */
#include "simplelink.h"
#include "BCDS_Basics.h"
#include "BCDS_Assert.h"
#include "FreeRTOS.h"
#include "timers.h"
#include "BCDS_WlanConnect.h"
#include "BCDS_NetworkConfig.h"
#include <Serval_Types.h>
#include <Serval_Basics.h>
#include <Serval_Ip.h>
#include "BCDS_Retcode.h"
#include "BCDS_Accelerometer.h"
#include "XdkSensorHandle.h"

/* own header files */
#include "SendDataOverUdp.h"

/* constant definitions ***************************************************** */

#define n 20
#define d 5
#define max_x 800
#define max_y 800
#define max_z 800



/**
 * This buffer holds the data to be sent to server via UDP
 * */
static uint16_t bsdBuffer_mau[BUFFER_SIZE] = { (uint16_t) ZERO };
/**
 * Timer handle for connecting to wifi and obtaining the IP address
 */
xTimerHandle wifiConnectTimerHandle_gdt = NULL;
/**
 * Timer handle for periodically sending data over wifi
 */
xTimerHandle wifiSendTimerHandle = NULL;

/* global variables ********************************************************* */

int alarm;
int accident;
int j;
int diff_x[n],diff_y[n],diff_z[n];
int sum_diff_x, sum_diff_y, sum_diff_z, backward;
int accel_array[n][3];


/* local functions ********************************************************** */
/**
 *  @brief
 *      Function to initialize the wifi network send application. Create timer task
 *      to start WiFi Connect and get IP function after one second. After that another timer
 *      to send data periodically.
 */
void init(void)
{

	Accelerometer_init(xdkAccelerometers_BMA280_Handle);
	alarm=0;
	accident=0;
    for (int a = 0; a < n; a++)
    {
    	diff_x[a]=0;
    	diff_y[a]=0;
    	diff_z[a]=0;
    	for (int b=0; b<3; b++)
    		accel_array[a][b]= 0;
     }

    sum_diff_x=0;
    sum_diff_y=0;
    sum_diff_z=0;
    backward=n-d;



	uint32_t Ticks = UINT32_C(200);

    if (Ticks != UINT32_MAX) /* Validated for portMAX_DELAY to assist the task to wait Infinitely (without timing out) */
    {
        Ticks /= portTICK_RATE_MS;
    }
    if (UINT32_C(0) == Ticks) /* ticks cannot be 0 in FreeRTOS timer. So ticks is assigned to 1 */
    {
        Ticks = UINT32_C(1);
    }
    /* create timer task*/
    wifiConnectTimerHandle_gdt = xTimerCreate((char * const ) "wifiConnect", Ticks, TIMER_AUTORELOAD_OFF, NULL, wifiConnectGetIP);
    wifiSendTimerHandle = xTimerCreate((char * const ) "wifiSend", Ticks, TIMER_AUTORELOAD_ON, NULL, wifiSend);

    if ((wifiConnectTimerHandle_gdt != NULL) && (wifiSendTimerHandle != NULL))
    {
        /*start the wifi connect timer*/
        if ( xTimerStart(wifiConnectTimerHandle_gdt, TIMERBLOCKTIME) != pdTRUE)
        {
            assert(false);
        }
    }
    else
    {
        /* Assertion Reason: "Failed to create timer task during initialization"   */
        assert(false);
    }

}

/**
 * @brief This is a template function where the user can write his custom application.
 *
 */
void appInitSystem(xTimerHandle xTimer)
{
    BCDS_UNUSED(xTimer);
    /*Call the WNS module init API */
    init();
}

/**
 * @brief Opening a UDP client side socket and sending data on a server port
 *
 * This function opens a UDP socket and tries to connect to a Server SERVER_IP
 * waiting on port SERVER_PORT.
 * Then the function will send periodic UDP packets to the server.
 * 
 * @param[in] port
 *					destination port number
 *
 * @return         returnTypes_t:
 *                                  SOCKET_ERROR: when socket has not opened properly
 *                                  SEND_ERROR: when 0 transmitted bytes or send error
 *                                  STATUS_OK: when UDP sending was successful
 */

static returnTypes_t bsdUdpClient(uint16_t port)
{
    static uint16_t counter = UINT16_C(0);
    SlSockAddrIn_t Addr;
    uint16_t AddrSize = (uint16_t) ZERO;
    int16_t SockID = (int16_t) ZERO;
    int16_t Status = (int16_t) ZERO;

    /* copies the dummy data to send array , the first array element is the running counter to track the number of packets send so far*/
    bsdBuffer_mau[0] = (uint16_t) counter;
    bsdBuffer_mau[1] = 0xAA55;
    bsdBuffer_mau[2] = 0xBB66;
    bsdBuffer_mau[3] = 0xCC77;
    Addr.sin_family = SL_AF_INET;
    Addr.sin_port = sl_Htons((uint16_t) port);
    Addr.sin_addr.s_addr = sl_Htonl(SERVER_IP);
    AddrSize = sizeof(SlSockAddrIn_t);

//    printf("sending\r\n");

    SockID = sl_Socket(SL_AF_INET, SL_SOCK_DGRAM, (uint32_t) ZERO); /**<The return value is a positive number if successful; other wise negative*/
    if (SockID < (int16_t) ZERO)
    {
        /* error case*/
        return (SOCKET_ERROR);
    }

    //get the accelometer data
    Accelerometer_XyzData_T getaccelData = { INT32_C(0), INT32_C(0), INT32_C(0) };

//    }
    if (RETCODE_OK == Accelerometer_readXyzGValue(xdkAccelerometers_BMA280_Handle, &getaccelData))
    {


    	//Recieve data from acceleration sensor, and store it in a nX3 maxtrix
    	accel_array[j][1] = getaccelData.xAxisData;
    	accel_array[j][2] = getaccelData.yAxisData;
    	accel_array[j][3] = getaccelData.zAxisData;

    	//Calculate diiference between 2 connsecutive measurments
    	if (j==0)
    	{
    		diff_x[0]=accel_array[0][1] - accel_array[n-1][1];
    	    diff_y[0]=accel_array[0][2] - accel_array[n-1][2];
    	    diff_z[0]=accel_array[0][3] - accel_array[n-1][3];
    	}
    	else
    	{
    	   	diff_x[j] = accel_array[j][1] - accel_array[j-1][1];
    		diff_y[j] = accel_array[j][2] - accel_array[j-1][2];
    	 	diff_z[j] = accel_array[j][3] - accel_array[j-1][3];
    	}

    	//Sum the difference between the last 5 measurments
   		sum_diff_x = sum_diff_x + diff_x[j] - diff_x[backward];
   		sum_diff_y = sum_diff_y + diff_y[j] - diff_y[backward];
   		sum_diff_z = sum_diff_z + diff_z[j] - diff_z[backward];

//	 	printf("j=%d,backward=%d,diff_x[j]=%d,diff_x[backward]=%d,sum=%d\n",j,backward,diff_x[j],diff_x[backward],sum_diff_x);
	 	int x_problem = (sum_diff_x < -max_x);
	 	int y_problem = (sum_diff_y < -max_y);
		int z_problem = (sum_diff_z < -max_z);
   		accident = (x_problem + y_problem + z_problem)>1;

	 	if (x_problem) printf("x_problem");
	 	if (y_problem) printf("y_problem");
	 	if (z_problem) printf("z_problem");
	 	if (accident) printf("\naccident!!");

    	if (j==n-1)
    		j=0;
    	else
    		j++;
    	if (backward==n-1)
    		backward=0;
    	else
    		backward++;
    }
    else
    {
        printf("Accelerometer Gravity XYZ Data read FAILED\n\r");
    }





    char *note;
    if (accident==0)
    {
    	note = "world";
    	alarm++;
    }
    else
    {
    	note = "hello";
    	alarm=0;
    }

    Status = sl_SendTo(SockID, note, 6, (uint32_t) ZERO, (SlSockAddr_t *) &Addr, AddrSize);/**<The return value is a number of characters sent;negative if not successful*/

    printf ("send %d char, %s \n",Status, note);

    /*Check if 0 transmitted bytes sent or error condition*/
    if (Status <= (int16_t) ZERO)
    {
        Status = sl_Close(SockID);
        if (Status < 0)
        {
            return (SEND_ERROR);
        }
        return (SEND_ERROR);
    }
    Status = sl_Close(SockID);
    if (Status < 0)
    {
        return (SEND_ERROR);
    }
    counter++;
    return (STATUS_OK);
}
/**
 *  @brief
 *      Function to periodically send data over WiFi as UDP packets. This is run as an Auto-reloading timer.
 *
 *  @param [in ] xTimer - necessary parameter for timer prototype
 */
static void wifiSend(xTimerHandle xTimer)
{
    BCDS_UNUSED(xTimer);
    if (STATUS_OK != bsdUdpClient(SERVER_PORT))
    {
        /* assertion Reason:  "Failed to  send udp packet" */
        assert(false);
    }
}

/**
 *  @brief
 *      Function to connect to wifi network and obtain IP address
 *
 *  @param [in ] xTimer
 */
static void wifiConnectGetIP(xTimerHandle xTimer)
{
    BCDS_UNUSED(xTimer);

    NetworkConfig_IpSettings_T myIpSettings;
    memset(&myIpSettings, (uint32_t) 0, sizeof(myIpSettings));
    char ipAddress[PAL_IP_ADDRESS_SIZE] = { 0 };
    Ip_Address_T* IpaddressHex = Ip_getMyIpAddr();
    WlanConnect_SSID_T connectSSID;
    WlanConnect_PassPhrase_T connectPassPhrase;
    Retcode_T ReturnValue = (Retcode_T)RETCODE_FAILURE;
    int32_t Result = INT32_C(-1);

    if (RETCODE_OK != WlanConnect_Init())
    {
        printf("Error occurred initializing WLAN \r\n ");
        return;
    }

    printf("Connecting to %s \r\n ", WLAN_CONNECT_WPA_SSID);

    connectSSID = (WlanConnect_SSID_T) WLAN_CONNECT_WPA_SSID;
    connectPassPhrase = (WlanConnect_PassPhrase_T) WLAN_CONNECT_WPA_PASS;
    ReturnValue = NetworkConfig_SetIpDhcp(NULL);
    if (ReturnValue)
    {
        printf("Error in setting IP to DHCP\n\r");
        return;
    }

    if (RETCODE_OK == WlanConnect_WPA(connectSSID, connectPassPhrase, NULL))
    {
        ReturnValue = NetworkConfig_GetIpSettings(&myIpSettings);
        if (RETCODE_OK == ReturnValue)
        {
            *IpaddressHex = Basics_htonl(myIpSettings.ipV4);
            Result = Ip_convertAddrToString(IpaddressHex, ipAddress);
            if (Result < 0)
            {
                printf("Couldn't convert the IP address to string format \r\n ");
                return;
            }
            printf("Connected to WPA network successfully. \r\n ");
            printf(" Ip address of the device: %s \r\n ", ipAddress);
        }
        else
        {
            printf("Error in getting IP settings\n\r");
            return;
        }
    }
    else
    {
        printf("Error occurred connecting %s \r\n ", WLAN_CONNECT_WPA_SSID);
        return;
    }

    /* After connection start the wifi sending timer*/
    if (xTimerStart(wifiSendTimerHandle, TIMERBLOCKTIME) != pdTRUE)
    {
        assert(false);
    }
}

/* global functions ********************************************************* */

/** ************************************************************************* */
