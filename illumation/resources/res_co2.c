#include "contiki.h"
#include <limits.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "coap-engine.h"


static void res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(res_co2,
                  "title=\"CO2\";rt=\"\";obs",
                  res_get_handler,
                  NULL,
                  NULL,
                  NULL);

static void
res_get_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
    int co2_level = (float)(rand() % 1800 + 200);   

    coap_set_header_content_format(response, APPLICATION_JSON);
    snprintf((char *)buffer, COAP_MAX_CHUNK_SIZE, "{\"co2_level\": %d}", co2_level);
    coap_set_payload(response, buffer, strlen((char *)buffer));

}

