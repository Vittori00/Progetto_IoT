#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"
#include "random.h"
#include "irrigation_model.h" // emlearn generated model
#include "eml_trees.h"        // Correct header for tree model functions
#include "eml_common.h"       // Common header for emlearn

/* Log configuration */
#include "coap-log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_APP

#define SERVER_EP "coap://[fd00::202:2:2:2]:5683"

char *service_url = "/soil";

#define TOGGLE_INTERVAL 10

PROCESS(er_example_client, "Erbium Example Client");
AUTOSTART_PROCESSES(&er_example_client);

static struct etimer et;
float features[] = { 1, 0, 0 };
int moisture = 0;
int temperature = 0;

void client_chunk_handler(coap_message_t *response)
{
    const uint8_t *chunk;

    if (response == NULL)
    {
        puts("Request timed out");
        return;
    }

    coap_get_payload(response, &chunk);

    // Assuming the response payload is in JSON format like: {"moisture": 50, "temperature": 25}
    sscanf((char *)chunk, "{\"moisture\": %d, \"temperature\": %d}", &moisture, &temperature);
}

PROCESS_THREAD(er_example_client, ev, data)
{
    static coap_endpoint_t server_ep;
    static coap_message_t request[1];

    PROCESS_BEGIN();

    coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

    etimer_set(&et, TOGGLE_INTERVAL * CLOCK_SECOND);

    while (1)
    {
        PROCESS_YIELD();

        if (etimer_expired(&et))
        {

            coap_init_message(request, COAP_TYPE_CON, COAP_GET, 0);
            coap_set_header_uri_path(request, service_url);

            COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);

            printf("\nMoisture: %d, Temperature: %d\n", moisture, temperature);
            features[1] = moisture;
            features[2] = temperature;
            
            int class_idx = eml_trees_predict(&irrigation_model, features, 3);
            
            printf("Irrigation needed: %d\n", class_idx);
            etimer_reset(&et);
        }
    }
    PROCESS_END();
}
