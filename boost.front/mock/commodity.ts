import {Request, Response} from 'express';

const mockListAllCommodities = (req: Request, res: Response) => {
    res.json({
        success: true,
        data: [
            {
                serviceId: "service-example",
                commodityName: "示例商品",
                description: "这是一个示例商品.",
                version: "1.0",
                commodityStatus: "Online",
                commodityCode: "exampleCommodityCode001",
                unitPrice: 100.0,
            },
        ],
    });
};

const mockCommodityList: API.CommodityDTO[] = [
    {
        commodityCode: "commodity001",
        commodityName: "Example Commodity",
        description: "This is an example commodity description.",
    },
];

const createOrUpdateCommodity = (req: Request, res: Response) => {
    res.json({
        code: '200',
        message: 'Success',
    });
};

const deleteCommodity = (req: Request, res: Response) => {
    res.json({
        code: '200',
        message: 'Commodity deleted successfully',
    });
};

const getCommodity = (req: Request, res: Response) => {
    const { commodityCode } = req.body;
    const commodity = mockCommodityList.find(item => item.commodityCode === commodityCode);
    res.json(commodity || {});
};

const getCommodityPrice = (req: Request, res: Response) => {
    res.json({
        price: 100.0,
        currency: 'USD',
    });
};

export default {
    'POST /api/listAllCommodities': mockListAllCommodities,
    'POST /api/createCommodity': createOrUpdateCommodity,
    'DELETE /api/deleteCommodity': deleteCommodity,
    'POST /api/spi/getCommodity': getCommodity,
    'POST /api/spi/getCommodityPrice': getCommodityPrice,
    'PUT /api/updateCommodity': createOrUpdateCommodity,
};
