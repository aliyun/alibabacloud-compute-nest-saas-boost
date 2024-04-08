import {Request, Response} from 'express';

const mockSpecificationList: API.CommoditySpecificationDTO[] = [
    {
        commodityCode: "commodity001",
        specificationName: "Example Specification",
        unitPrice: 100,
        currency: "CNY",
        payPeriodUnit: "Month",
        payPeriods: "[1,3,6,12]",
    },
];

const createCommoditySpecification = (req: Request, res: Response) => {
    res.json({
        code: '200',
        message: 'Specification created successfully',
    });
};

const deleteCommoditySpecification = (req: Request, res: Response) => {
    res.json({
        code: '200',
        message: 'Specification deleted successfully',
    });
};

const listAllSpecifications = (req: Request, res: Response) => {
    const listResult: API.ListResultCommoditySpecificationDTO_ = {
        code: '200',
        message: 'Success',
        data: mockSpecificationList,
        count: mockSpecificationList.length,
    };
    res.json(listResult);
};

const updateCommoditySpecification = (req: Request, res: Response) => {
    res.json({
        code: '200',
        message: 'Specification updated successfully',
        data: req.body
    });
};

export default {
    'POST /api/createCommoditySpecification': createCommoditySpecification,
    'DELETE /api/deleteCommoditySpecification': deleteCommoditySpecification,
    'POST /api/listAllSpecifications': listAllSpecifications,
    'PUT /api/updateCommoditySpecification': updateCommoditySpecification,
};
