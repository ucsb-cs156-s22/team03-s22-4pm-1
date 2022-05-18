const helpRequestFixtures = {
    oneHelpRequest: {
        "id": 0,
        "requesterEmail": "andywu@ucsb.edu",
        "teamId": "1",
        "tableOrBreakoutRoom": "1",
        "requestTime": "2022-05-18T08:00:00",
        "explanation": "Help with APIs",
        "solved": false
    },
    threeHelpRequest: [
        {
            "id": 1,
            "requesterEmail": "joeschmo@ucsb.edu",
            "teamId": "2",
            "tableOrBreakoutRoom": "2",
            "requestTime": "2022-05-17T10:00:00",
            "explanation": "index table issues",
            "solved": false
        },
        {
            "id": 2,
            "requesterEmail": "coconut@ucsb.edu",
            "teamId": "6",
            "tableOrBreakoutRoom": "8",
            "requestTime": "2022-05-15T07:00:00",
            "explanation": "mvn won't compile",
            "solved": false
        },
        {
            "id": 3,
            "requesterEmail": "pineapplecat@ucsb.edu",
            "teamId": "5",
            "tableOrBreakoutRoom": "7",
            "requestTime": "2022-04-29T15:00:00",
            "explanation": "Happy Cows is Slow",
            "solved": true
        },
    ]
};


export { helpRequestFixtures };