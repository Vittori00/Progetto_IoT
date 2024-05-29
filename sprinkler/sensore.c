#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"
#include "../cJSON/cJSON.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP
#define SAMPLING_TIME 10
#define SERVER_EP "coap://[fd00::1]:5683" // localhost ip6
#define GOOD_ACK 0



void client_chunk_handler(coap_message_t *response)
{
  const uint8_t *chunk;
  if (response == NULL)
  {
    printf("Request timed out\n");
    return;
  }
  int len = coap_get_payload(response, &chunk);
  char payload[len + 1];
  memcpy(payload, chunk, len);
  payload[len] = '\0'; // Ensure null-terminated string
  printf("Response: %i\n", response->code);
  if (response->code == GOOD_ACK)
  {
    printf("Registration successful\n");
  }
  else
  {
    printf("Registration failed\n");
  }
}
extern coap_resource_t  res_soil; 
PROCESS(er_example_server, "Erbium Example Server");
AUTOSTART_PROCESSES(&er_example_server);

PROCESS_THREAD(er_example_server, ev, data)
{
   static coap_endpoint_t server_ep;
  static coap_message_t request[1];

  PROCESS_BEGIN();
  coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
  // Registration Process
  printf("REGISTRATION TO THE SERVER...\n");
  coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
  coap_set_header_uri_path(request, "/registration");
  printf("MESSAGGIO INIZIALIZZATO\n");
  cJSON *package = cJSON_CreateObject();

  cJSON_AddStringToObject(package, "s", "sensor1");
  cJSON_AddStringToObject(package, "t", "sensor");
  cJSON_AddNumberToObject(package, "c", SAMPLING_TIME);
  char *payload = cJSON_PrintUnformatted(package);
  if (payload == NULL)
  {
    LOG_ERR("Failed to print JSON object\n");
    cJSON_Delete(package);
    PROCESS_EXIT();
  }
  printf("il payload %s  lenght  %ld \n", payload, strlen(payload));
  coap_set_payload(request, (uint8_t *)payload, strlen(payload));
  COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);
  printf("REGISTRATION TO THE SERVER COMPLETED\n");


  coap_activate_resource(&res_soil, "soil");

  while(1) {
    
    PROCESS_WAIT_EVENT();

  }                             

  PROCESS_END();
}
