
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

extern coap_resource_t res_co2, res_luce, res_phase;
static struct etimer et;
PROCESS(illumination_server, "Illumination Server");
AUTOSTART_PROCESSES(&illumination_server);

PROCESS_THREAD(illumination_server, ev, data)
{
  PROCESS_BEGIN();

  PROCESS_PAUSE();

  LOG_INFO("Starting illunation Server\n");

  coap_activate_resource(&res_co2, "co2");
  coap_activate_resource(&res_luce, "light");
  coap_activate_resource(&res_phase, "phase");
  etimer_set(&et, CLOCK_SECOND * 4);
  while(1) {
    PROCESS_WAIT_EVENT();
    
    if (ev == PROCESS_EVENT_TIMER && data == &et)
    {
      printf("Event triggered\n");
   
      res_co2.trigger();
      res_luce.trigger();
      res_phase.trigger();

      etimer_set(&et, CLOCK_SECOND * 10);
    }
  }                             

  PROCESS_END();
}
