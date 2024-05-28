#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

extern coap_resource_t res_co2, res_light, res_phase , res_sampling;
static struct etimer et;
extern int sampling;
PROCESS(illumination_server, "Illumination Server");
AUTOSTART_PROCESSES(&illumination_server);

PROCESS_THREAD(illumination_server, ev, data)
{
  PROCESS_BEGIN();

  PROCESS_PAUSE();

  LOG_INFO("Starting illunation Server\n");

  coap_activate_resource(&res_co2, "co2");
  coap_activate_resource(&res_light, "light");
  coap_activate_resource(&res_phase, "phase");
  coap_activate_resource(&res_sampling, "sampling");
  etimer_set(&et, CLOCK_SECOND * sampling);
  while(1) {
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