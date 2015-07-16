require 'test_helper'

class StaticPagesControllerTest < ActionController::TestCase
  test "should get home" do
    get :home
    assert_response :success
  end

  test "should get info" do
    get :info
    assert_response :success
  end

  test "should get HelloWorld" do
    get :HelloWorld
    assert_response :success
  end

  test "should get Catalog" do
    get :Catalog
    assert_response :success
  end

  test "should get Databases" do
    get :Databases
    assert_response :success
  end

  test "should get SensorInsert" do
    get :SensorInsert
    assert_response :success
  end

  test "should get SensorRead" do
    get :SensorRead
    assert_response :success
  end

  test "should get Spatial" do
    get :Spatial
    assert_response :success
  end

  test "should get Text" do
    get :Text
    assert_response :success
  end

  test "should get TimeSeries" do
    get :TimeSeries
    assert_response :success
  end

end
