#include "contiki.h"
#include <limits.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "coap-engine.h"
#include "../global_variables.h"

#define MAX_AGE 60
int sampling = 10; 

static void res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(res_sampling,
         "title=\"Moisture\";rt=\"Temperature\";obs",
         NULL,
         res_post_handler,
         NULL,
         NULL);

static void
res_post_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    const uint8_t *payload = NULL;
    coap_get_payload(request, &payload);
    sscanf((const char *)payload, "{\"sampling\": %d}", &sampling);
    printf("Tempo di campionamento aggiornato a: %d secondi \n", sampling);
    coap_set_payload(response, &payload);
}
