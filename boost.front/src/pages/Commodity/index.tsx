import React, {useEffect, useState} from 'react';
import {Button, message, Modal, Table} from 'antd';
import CommodityForm from './CommodityForm';
import {deleteCommodity, listAllCommodities} from "@/services/backend/commodity";
import SpecificationModal from "@/pages/Commodity/SpeicificationModal";
import {commodityColumns} from "@/pages/Commodity/common";

const CommodityList: React.FC = () => {
    const [commodities, setCommodities] = useState<API.CommodityDTO[]>([]);
    const [isCommodityModalVisible, setIsCommodityModalVisible] = useState(false);
    const [isSpecificationModalVisible, setIsSpecificationModalVisible] = useState(false);
    const [selectedCommodity, setSelectedCommodity] = useState<API.CommodityDTO | null>(null);

    useEffect(() => {
        fetchCommodities();
    }, []);

    const fetchCommodities = async () => {
        try {
            const response = await listAllCommodities({});
            setCommodities(response.data || []);
        } catch (error) {
            message.error('Failed to fetch commodities');
        }
    };

    const handleDelete = async (commodityCode: string) => {
        try {
            await deleteCommodity({ commodityCode });
            deleteCommodity({ commodityCode });
            message.success('Commodity deleted successfully');
        } catch (error) {
            message.error('Failed to delete commodity');
        }
    };

    const handleCloseSpecificationModal = () => {
        setIsSpecificationModalVisible(false);
    };

    const handleNewCommodity = () => {
        setSelectedCommodity(null);
        setIsCommodityModalVisible(true);
    };

    const handleEditCommodity = (commodity: API.CommodityDTO) => {
        setSelectedCommodity(commodity);
        setIsCommodityModalVisible(true);
    };

    const handleSpecifications = (commodity: API.CommodityDTO) => {
        setSelectedCommodity(commodity);
        setIsSpecificationModalVisible(true);
    };

    const columns = [
        ...commodityColumns,
        {
            title: 'Action',
            key: 'action',
            render: (text: string, record: API.CommodityDTO) => (
                <>
                    <Button type="link" onClick={() => handleEditCommodity(record)}>Edit</Button>
                    <Button type="link" onClick={() => handleDelete(record.commodityCode)}>Delete</Button>
                    <Button type="link" onClick={() => handleSpecifications(record)}>Manage Specifications</Button>
                </>
            ),
        },
    ];

    return (
        <>
            <Button type="primary" onClick={handleNewCommodity}>New Commodity</Button>
            <Table dataSource={commodities} columns={columns} rowKey="commodityCode" />
            <Modal
                title={selectedCommodity ? 'Edit Commodity' : 'New Commodity'}
                open={isCommodityModalVisible}
                onCancel={() => setIsCommodityModalVisible(false)}
                footer={null}
            >
                {isCommodityModalVisible && (
                    <CommodityForm commodity={selectedCommodity} />
                )}
            </Modal>
            <Modal
                title="Manage Specifications"
                visible={isSpecificationModalVisible}
                onCancel={() => setIsSpecificationModalVisible(false)}
                destroyOnClose={true}
                footer={null}
            >
                {isSpecificationModalVisible && selectedCommodity && (
                    <SpecificationModal commodity={selectedCommodity}  onClose={handleCloseSpecificationModal} visible={isSpecificationModalVisible}/>
                )}
            </Modal>
        </>
    );
};

export default CommodityList;
