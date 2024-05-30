#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "machine_learning/temperature_prediction.h"
#include "../cJSON-master/cJSON.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../global_variable/global_variables.h"

static int new_co2 = 0;
static int new_light = 0;
static int new_phase = 0;

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
    cJSON *row = cJSON_CreateObject();
    cJSON_AddNumberToObject(row, "c", cJSON_CreateNumber(new_co2));
    cJSON_AddNumberToObject(row, "l", cJSON_CreateNumber(new_light));
    cJSON_AddNumberToObject(row, "p", cJSON_CreateNumber(new_phase));

    char *payload = cJSON_Print(row);

    if (payload == NULL)
    {
        LOG_ERR("Failed to print JSON object\n");
        cJSON_Delete(package);
        PROCESS_EXIT();
    }

    printf("payload: %s length: %ld\n", payload, strlen(payload));

    coap_set_payload(response, cJSON_Print(row), strlen(cJSON_Print(row)));
}

static void
res_event_handler(void){
    coap_notify_observers(&res_observation);
}