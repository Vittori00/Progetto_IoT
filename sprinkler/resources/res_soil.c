#include "contiki.h"
#include <limits.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "coap-engine.h"
#include "../../cJSON/cJSON.h"


#define MAX_AGE      60
static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);
static void res_event_handler(void);
EVENT_RESOURCE (res_soil,
                  "title=\"Moisture\";rt=\"Temperature\";obs",
                  res_get_handler,
                  NULL,
                  NULL,
                  NULL,
                  res_event_handler);

static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    int moisture = (float)(rand() % 1018 + 4);   // Valore di umidità casuale
    int temperature = (float)(rand() % 35 + 10); // Valore di temperatura casuale
    cJSON *row = cJSON_CreateObject();

    cJSON_AddNumberToObject(row, "moisture", moisture);
    cJSON_AddNumberToObject(row, "temperature", temperature);


    char *payload = cJSON_PrintUnformatted(row);

    printf("payload: %s length: %ld\n", payload, strlen(payload));
    coap_set_payload(response, (uint8_t *)payload, strlen(payload));
    /*
   
    coap_set_header_content_format(response, APPLICATION_JSON);
    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"moisture\": %d, \"temperature\": %d}", moisture, temperature);
    coap_set_payload(response, buffer, strlen((char *)buffer)); */

    coap_set_header_max_age(response, MAX_AGE);
}

// Event handler function
static void res_event_handler(void)
{
    coap_notify_observers(&res_soil);
}