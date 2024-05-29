#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP
// #define SERVER_EP "coap://[fd00::202:2:2:2]:5683"
#define SERVER_EP "coap://[fd00::1]:5683" // localhost ip6

void client_chunk_handler(coap_message_t *response)
{
  const uint8_t *chunk;

  if (response == NULL)
  {
    LOG_INFO("Request timed out");
    return;
  }

  int len = coap_get_payload(response, &chunk);
  LOG_INFO("Received %d bytes:\n", len);
  LOG_INFO("Response: %s", chunk);
}

extern coap_resource_t res_co2, res_light, res_phase, res_sampling;
static struct etimer et;
extern int sampling;
PROCESS(illumination_server, "Illumination Server");
AUTOSTART_PROCESSES(&illumination_server);

PROCESS_THREAD(illumination_server, ev, data)
{
  
  static coap_endpoint_t server_ep;
  static coap_message_t request[1];

  PROCESS_BEGIN();
  // Registration Process
  coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);
  coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
  coap_set_header_uri_path(request, "/registration");
  LOG_INFO("Registering to the CoAP Server\n");
  COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);


  coap_activate_resource(&res_co2, "co2");
  coap_activate_resource(&res_light, "light");
  coap_activate_resource(&res_phase, "phase");
  coap_activate_resource(&res_sampling, "sampling");

  etimer_set(&et, CLOCK_SECOND * sampling);
  while (1)
  {
    PROCESS_WAIT_EVENT();

    if (ev == PROCESS_EVENT_TIMER && data == &et)
    {
      printf("Event triggered\n");

      res_co2.trigger();
      res_light.trigger();
      res_phase.trigger();

      etimer_set(&et, CLOCK_SECOND * sampling);
    }
  }

  PROCESS_END();
}