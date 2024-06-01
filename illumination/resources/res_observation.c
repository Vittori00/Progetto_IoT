#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "../../cJSON/cJSON.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../global_variables.h"
/* Log configuration */
#include "coap-log.h"
#define LOG_MODULE "App"

#define LOG_LEVEL LOG_LEVEL_APP
extern int light_attuatore;
extern int co2;
extern int fase;

static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);

EVENT_RESOURCE(res_observation,
               "title=\"Observation\";rt=\"\";obs",
               res_get_handler,
               NULL,
               NULL,
               NULL,
               res_event_handler);

static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    printf("observation sending\n");
    cJSON *row = cJSON_CreateObject();
    cJSON_AddNumberToObject(row, "c", co2);
    cJSON_AddNumberToObject(row, "l", light_attuatore);
    cJSON_AddNumberToObject(row, "p", fase);
    char *payload = cJSON_PrintUnformatted(row);
    //coap_set_header_content_format(response, APPLICATION_JSON);
    printf("payload: %s length: %ld\n", payload, strlen(payload));
    coap_set_header_content_format(response, APPLICATION_JSON);
    coap_set_payload(response, (uint8_t *)payload, strlen(payload));
}

static void res_event_handler(void)  {
    printf("Sending notification\n");
    coap_notify_observers(&res_observation);
     printf(" notification sent\n");
}