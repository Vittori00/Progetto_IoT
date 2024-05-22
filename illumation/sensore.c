
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

PROCESS(illumination_server, "Erbium Example Server");
AUTOSTART_PROCESSES(&illumination_server);

PROCESS_THREAD(illumination_server, ev, data)
{
  PROCESS_BEGIN();

  PROCESS_PAUSE();

  LOG_INFO("Starting Erbium Example Server\n");

  coap_activate_resource(&res_co2, "co2");
  coap_activate_resource(&res_luce, "light");
  coap_activate_resource(&res_phase, "phase");

  while(1) {
    PROCESS_WAIT_EVENT();

  }                             

  PROCESS_END();
}
